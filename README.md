# arpSignatures

Plugin for Paper 26.1.2+ (and compatible forks) as well as Folia, that allows players to sign their items with custom text using a simple command. It features configurable signature formats, permission-based styles, placeholder support, customizable validation rules, and text filters, making it easy to adapt the signing system to your server.

## Download

- Releases page: https://github.com/ArpeggioDevelopment/arpSignatures/releases

## Getting Started

1. Install the plugin by placing the `.jar` file into your server's `plugins/` folder.
2. Start the server.
3. Configure the plugin to your liking.
4. Players with the `arpsignatures.use` permission can sign the item they are holding with: `/sign <text>`

That's it! Your players can now create custom signatures on supported items.

## Compatibility

This plugin requires **Java 25 or newer** and supports **Paper 26.1.2+** and compatible Paper forks. **Folia** is fully supported using the same plugin JAR - no separate build is required.

Paper 26.1.2 is the minimum supported stable release from the [Paper versions requiring Java 25](https://docs.papermc.io/paper/getting-started/#requirements).

## Building

Build the project using Gradle with **JDK 25**:

```bash
./gradlew build
```

The build generates two JAR files in the `plugin/build/libs` directory. The shaded JAR, named `arpSignatures-v<version>.jar`, is the plugin you should use on your server.

## Support

If you encounter any issues or have suggestions, please join our Discord server and create a post in the `#issues` channel. [Click to join.](https://discord.gg/UYvVRgSJFX)
