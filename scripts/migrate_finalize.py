#!/usr/bin/env python3
"""
Migrate kdu_jni Java files from deprecated finalize() to the modern AutoCloseable pattern.

For each class that uses finalize() for native resource cleanup, this script:
  1. Adds `implements AutoCloseable` to the class declaration.
  2. Renames `public void finalize()` to `@Override public void close()` (same body).
  3. Inserts a new `finalize()` delegate that calls `close()`, annotated with
     @Override and @SuppressWarnings("removal") so it acts as a GC safety-net
     for callers that do not yet use try-with-resources.

Usage:
    python3 scripts/migrate_finalize.py [--dry-run] [path/to/kdu_jni]

The default target directory is src/main/java/kdu_jni relative to the current
working directory.
"""

import argparse
import glob
import os
import re
import sys

# ---------------------------------------------------------------------------
# Exact text of the finalize() block as it appears in every generated file.
# The body is always identical – only the surrounding class differs.
# ---------------------------------------------------------------------------
OLD_FINALIZE = (
    "  public void finalize() {\n"
    "    if ((_native_ptr & 1) != 0)\n"
    "      { // Resource created and not donated\n"
    "        Native_destroy();\n"
    "      }\n"
    "  }"
)

NEW_CLOSE_AND_FINALIZE = (
    "  @Override\n"
    "  public void close() {\n"
    "    if ((_native_ptr & 1) != 0)\n"
    "      { // Resource created and not donated\n"
    "        Native_destroy();\n"
    "      }\n"
    "  }\n"
    "  @Override\n"
    "  @SuppressWarnings(\"removal\")\n"
    "  protected void finalize() {\n"
    "    close();\n"
    "  }"
)

# ---------------------------------------------------------------------------
# Regex to match the class declaration line and capture its parts so we can
# inject "implements AutoCloseable".
#
# Handles all forms found in kdu_jni:
#   public class Foo {
#   public class Foo extends Bar {
#   public abstract class Foo {
#   public abstract class Foo extends Bar {
#
# None of the generated classes already use "implements", so we never need to
# append to an existing implements list.
# ---------------------------------------------------------------------------
CLASS_DECL_RE = re.compile(
    r"^(public (?:abstract )?class \w+(?:\s+extends \s*\w+)?)\s*\{",
    re.MULTILINE,
)


def transform(content: str) -> tuple[str, list[str]]:
    """Return (new_content, list_of_changes) for a single file's content."""
    changes: list[str] = []

    if OLD_FINALIZE not in content:
        return content, changes

    # ------------------------------------------------------------------
    # 1. Add "implements AutoCloseable" to the class declaration.
    # ------------------------------------------------------------------
    def _add_autocloseable(m: re.Match) -> str:
        return m.group(1) + " implements AutoCloseable {"

    new_content, n = CLASS_DECL_RE.subn(_add_autocloseable, content, count=1)
    if n == 1:
        changes.append("added 'implements AutoCloseable' to class declaration")
    else:
        # Should never happen – every file has exactly one top-level class.
        print(
            "  WARNING: could not find class declaration; skipping AutoCloseable injection",
            file=sys.stderr,
        )
        new_content = content

    # ------------------------------------------------------------------
    # 2. Replace finalize() body with close() + delegating finalize().
    # ------------------------------------------------------------------
    if OLD_FINALIZE in new_content:
        new_content = new_content.replace(OLD_FINALIZE, NEW_CLOSE_AND_FINALIZE, 1)
        changes.append("replaced finalize() with close() + delegating finalize()")
    else:
        print(
            "  WARNING: finalize() block not found after class-decl edit; "
            "content may already be transformed or has unexpected whitespace",
            file=sys.stderr,
        )

    return new_content, changes


def process_file(path: str, dry_run: bool) -> bool:
    """Process one file. Returns True if the file was (or would be) modified."""
    with open(path, "r", encoding="utf-8") as fh:
        original = fh.read()

    transformed, changes = transform(original)

    if not changes:
        return False

    print(f"  {'[DRY-RUN] ' if dry_run else ''}Modifying {os.path.basename(path)}")
    for change in changes:
        print(f"    • {change}")

    if not dry_run:
        with open(path, "w", encoding="utf-8") as fh:
            fh.write(transformed)

    return True


def main() -> int:
    parser = argparse.ArgumentParser(
        description="Migrate kdu_jni finalize() methods to AutoCloseable."
    )
    parser.add_argument(
        "directory",
        nargs="?",
        default=os.path.join("src", "main", "java", "kdu_jni"),
        help="Path to the kdu_jni source directory (default: src/main/java/kdu_jni)",
    )
    parser.add_argument(
        "--dry-run",
        action="store_true",
        help="Print what would change without writing any files.",
    )
    args = parser.parse_args()

    target_dir = args.directory
    if not os.path.isdir(target_dir):
        print(f"ERROR: directory not found: {target_dir}", file=sys.stderr)
        return 1

    java_files = sorted(glob.glob(os.path.join(target_dir, "*.java")))
    if not java_files:
        print(f"ERROR: no .java files found in {target_dir}", file=sys.stderr)
        return 1

    modified = 0
    skipped = 0

    print(
        f"{'[DRY-RUN] ' if args.dry_run else ''}Scanning {len(java_files)} "
        f"files in {target_dir} …\n"
    )

    for path in java_files:
        was_modified = process_file(path, dry_run=args.dry_run)
        if was_modified:
            modified += 1
        else:
            skipped += 1

    print(
        f"\nDone. "
        f"{'Would modify' if args.dry_run else 'Modified'}: {modified} file(s), "
        f"skipped (no change needed): {skipped} file(s)."
    )
    return 0


if __name__ == "__main__":
    sys.exit(main())
