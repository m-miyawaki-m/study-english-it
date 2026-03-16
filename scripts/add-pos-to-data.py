#!/usr/bin/env python3
"""Add part_of_speech data to existing NGSL words in data.sql.

Reads pos-data.json and rewrites NGSL INSERT statements in data.sql
to include the part_of_speech column.
"""

import json
import re
from pathlib import Path

SCRIPT_DIR = Path(__file__).resolve().parent
PROJECT_ROOT = SCRIPT_DIR.parent

POS_PATH = SCRIPT_DIR / "pos-data.json"
DATA_PATH = PROJECT_ROOT / "backend" / "src" / "main" / "resources" / "data.sql"


def escape_sql(value):
    if value is None:
        return "NULL"
    return "'" + str(value).replace("'", "''") + "'"


def main():
    with open(POS_PATH, encoding="utf-8") as f:
        pos_data = json.load(f)

    with open(DATA_PATH, encoding="utf-8") as f:
        content = f.read()

    updated = 0
    missing = 0

    # Match NGSL INSERT lines (with pronunciation_ipa already added)
    pattern = re.compile(
        r"INSERT INTO words \(word, meaning_general, meaning_it, pronunciation_ipa, frequency, source, level, known\) "
        r"VALUES \('([^']*)',"
    )

    lines = content.split('\n')
    new_lines = []

    for line in lines:
        m = pattern.match(line)
        if m:
            word = m.group(1)
            pos = pos_data.get(word) or pos_data.get(word.lower())
            if pos:
                # Add part_of_speech column
                line = line.replace(
                    "(word, meaning_general, meaning_it, pronunciation_ipa, frequency, source, level, known)",
                    "(word, meaning_general, meaning_it, pronunciation_ipa, part_of_speech, frequency, source, level, known)"
                )
                # Insert POS value after pronunciation_ipa value
                # Find the IPA value (4th value) and insert POS after it
                vals_start = line.index("VALUES (") + len("VALUES (")
                rest = line[vals_start:]

                # Parse values to find insertion point after 4th value (pronunciation_ipa)
                count = 0
                i = 0
                while i < len(rest) and count < 4:
                    if rest[i] == "'":
                        j = i + 1
                        while j < len(rest):
                            if rest[j] == "'" and (j + 1 >= len(rest) or rest[j + 1] != "'"):
                                break
                            if rest[j] == "'" and j + 1 < len(rest) and rest[j + 1] == "'":
                                j += 2
                                continue
                            j += 1
                        i = j + 1
                        count += 1
                    elif rest[i:i+4] == 'NULL':
                        i += 4
                        count += 1
                    elif rest[i] in (',', ' '):
                        i += 1
                    else:
                        i += 1

                # i is now right after the 4th value, skip ", "
                while i < len(rest) and rest[i] in (',', ' '):
                    i += 1

                # Insert POS before the 5th value (frequency)
                new_rest = rest[:i] + escape_sql(pos) + ", " + rest[i:]
                line = line[:vals_start] + new_rest
                updated += 1
            else:
                missing += 1
        new_lines.append(line)

    with open(DATA_PATH, "w", encoding="utf-8") as f:
        f.write('\n'.join(new_lines))

    print(f"Updated: {updated} words with part_of_speech")
    print(f"Missing POS: {missing} words")


if __name__ == "__main__":
    main()
