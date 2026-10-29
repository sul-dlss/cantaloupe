# Stanford release branch

Stanford deploys Cantaloupe from a release branch (e.g. `sul-2026-q4`) in
[sul-dlss/cantaloupe](https://github.com/sul-dlss/cantaloupe). That branch is
never edited by hand; it is regenerated from:

1. `develop` from [cantaloupe-project/cantaloupe](https://github.com/cantaloupe-project/cantaloupe)
2. the open upstream pull requests listed in [`prs`](prs)
3. the `sul-patches` branch: Stanford-only changes (Honeybadger reporting,
   and this directory), kept based on `develop`

Every deployed build is tagged (`v<version>`), so the release branch can be
force-pushed on each rebuild without losing the history of what was deployed.

## Setup

```sh
git clone https://github.com/sul-dlss/cantaloupe.git && cd cantaloupe
git remote add community https://github.com/cantaloupe-project/cantaloupe.git
```

## Cutting a release

```sh
git fetch origin
git switch --detach origin/sul-patches
sul/rebuild-release-branch.sh 5.0.9-sul-2026-q4-2
mvn verify
git push --force-with-lease origin sul-2026-q4
git tag v5.0.9-sul-2026-q4-2 && git push origin v5.0.9-sul-2026-q4-2
```

To see what changed since the last deploy, compare tags:
`git diff v5.0.9-sul-2026-q4-1 v5.0.9-sul-2026-q4-2`.

## Changing what goes in

- **Add or drop an upstream PR:** edit `sul/prs` on `sul-patches` and push.
  Remove a PR once it has been merged into `develop`.
- **Change a Stanford-only patch:** commit to `sul-patches` and push.
- **A PR conflicts with `develop` or with another PR:** fix it on the PR branch
  (merge `develop` into it) rather than in the release branch, so the fix
  survives the next rebuild. The script enables `git rerere`, which replays
  resolutions you've made before, but only in your own clone.
- **`sul-patches` conflicts with `develop`:** rebase `sul-patches` onto
  `community/develop` and force-push it.
