#!/usr/bin/env bash
#
# Rebuilds the Stanford release branch from scratch:
#
#   community/develop
#     + each open upstream PR listed in sul/prs (merged in order)
#     + the sul-patches branch (Stanford-only changes, including this script)
#     + a commit setting the release version in pom.xml
#
# The result is left on a local branch for testing; nothing is pushed.
#
# Usage: sul/rebuild-release-branch.sh <version> [branch]
#   e.g. sul/rebuild-release-branch.sh 5.0.9-sul-2026-q4-2
#
# Environment:
#   UPSTREAM  remote for cantaloupe-project/cantaloupe (default: community)
#   ORIGIN    remote for sul-dlss/cantaloupe (default: origin)
#   PATCHES   ref holding the Stanford patches (default: $ORIGIN/sul-patches)

set -euo pipefail

# Switching branches below rewrites this file, so run from a copy.
if [[ -z "${SUL_REBUILD_COPY:-}" ]]; then
  copy=$(mktemp)
  cp "$0" "$copy"
  SUL_REBUILD_COPY=1 exec bash "$copy" "$@"
fi

version=${1:?usage: $0 <version> [branch]}
branch=${2:-sul-2026-q4}
upstream=${UPSTREAM:-community}
origin=${ORIGIN:-origin}
patches=${PATCHES:-$origin/sul-patches}

if [[ -n "$(git status --porcelain --untracked-files=no)" ]]; then
  echo "Working tree has uncommitted changes; commit or stash them first." >&2
  exit 1
fi

git config rerere.enabled true

git fetch "$upstream" develop
git fetch "$origin"
prs=$(git show "$patches:sul/prs" | sed 's/#.*//' | tr -s '[:space:]' ' ')
for pr in $prs; do
  git fetch "$upstream" "+pull/$pr/head:refs/sul-rebuild/pr-$pr"
done

git switch -C "$branch" "$upstream/develop"
for pr in $prs; do
  git merge --no-ff --no-edit -m "Merge upstream PR #$pr" "refs/sul-rebuild/pr-$pr"
done
git merge --no-ff --no-edit -m "Merge Stanford patches ($patches)" "$patches"

mvn -q versions:set -DnewVersion="$version" -DgenerateBackupPoms=false
git commit -am "Bump version to $version"

cat <<EOF

Rebuilt $branch at version $version. Next:
  mvn verify
  git push --force-with-lease $origin $branch
  git tag v$version && git push $origin v$version
EOF
