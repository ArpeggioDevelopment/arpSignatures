# arpSignatures

Players can sign their items with custom text using a simple command. Signatures support configurable formats, permission-based styles, placeholders, validation rules, and filters.

## Features

- Sign items with custom text.
- Fully customizable signature formats.
- Multiple permission-based signature formats with priority support.
- Built-in placeholders (player, date, signature, and more).
- Customizable date format.
- Configurable minimum and maximum signature length.
- Blacklist/whitelist support for words and regex patterns.
- Item blacklist and whitelist support.
- Fully customizable messages.
- MiniMessage support.
- Lightweight, fast, and easy to configure.

## Commands & Permissions

| Command | Description                     | Permission |
| --- |---------------------------------| --- |
| `/sign <text>` | Signs the item you are holding. | `arpsignatures.use` |
| `/arpsignatures reload` | Reloads the plugin configuration.    | `arpsignatures.reload` |

## Configuration

### general.yml

```yaml
dateFormat: HH:mm:ss dd.MM.yyyy
restrictions:
  minCharacters: 3
  maxCharacters: 32
  itemFilter:
    # WHITELIST or BLACKLIST
    mode: BLACKLIST
    materials:
      - TRIPWIRE_HOOK
  signatureFilter:
    mode: BLACKLIST
    words: []
    regexes: []
permissions:
  # <signature>, <player>, <date>
  # Permissions are checked from top to bottom. If a player has multiple permissions, the first matching entry will be used.
  formats:
    default: <white><signature> <dark_gray>(<gray><player>, <date></gray>)
    arpsignatures.rainbow: <rainbow><signature> <gold>(<yellow><player>, <date></yellow>)
    arpsignatures.svip: <aqua><signature> <dark_gray>(<gray><player>, <date></gray>)
    arpsignatures.vip: <yellow><signature> <dark_gray>(<gray><player>, <date></gray>)
```

### messages.yml

```yaml
messages:
  # https://eternalcode.pl/docs/multification/format
  invalidUsage: '<red>Usage: {USAGE}'
  noPermission: <red>You don't have permission to execute this command. ({PERMISSIONS})
  # {SIGNATURE}, {MIN}, {MAX}
  notInRange: <red>Signature <dark_gray>(<white>{SIGNATURE}</white>)</dark_gray> must
    be in range between <dark_red>{MIN}-{MAX}</dark_red> characters.</red>
  noItem: <red>You can't sign air!
  blacklistedItem: <red>You can't sign this item.
  invalidSignature: <red>You can't use that signature!
  # {SIGNATURE}, {DATE}
  signed: '<green>Successfully signed your item with signature ''{SIGNATURE}'' at {DATE}. '
  reloading: <gray>Reloading configuration...
  reloaded: <green>Successfully reloaded configuration in {TIME}ms!
  reloadError: <red>There was an error trying to reload configuration. Check server
    logs for more information.
```

## Support

If you encounter any issues or have suggestions, please join our Discord server and create a post in the `#issues` channel.