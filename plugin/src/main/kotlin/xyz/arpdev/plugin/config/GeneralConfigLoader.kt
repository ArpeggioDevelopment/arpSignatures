package xyz.arpdev.plugin.config

import eu.okaeri.configs.ConfigManager
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer
import org.bukkit.Material
import java.io.File
import java.time.DateTimeException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.regex.PatternSyntaxException

class GeneralConfigLoader {

    fun load(file: File): GeneralConfig {
        val config = ConfigManager.create(GeneralConfig::class.java) { init ->
            init.configure { conf ->
                conf.configurer(YamlBukkitConfigurer())
                conf.bindFile(file)
                conf.removeOrphans(true)
            }
            init.saveDefaults()
            init.load()
        }

        validate(config)
        return config
    }

    private fun validate(config: GeneralConfig) {
        val restrictions = config.restrictions
        require(restrictions.minCharacters > 0) {
            "restrictions.minCharacters must be greater than zero."
        }
        require(restrictions.maxCharacters >= restrictions.minCharacters) {
            "restrictions.maxCharacters must be greater than or equal to restrictions.minCharacters."
        }

        val itemFilter = restrictions.itemFilter
        itemFilter.resolvedMaterials = buildSet {
            itemFilter.materials.forEachIndexed { index, name ->
                add(requireNotNull(Material.getMaterial(name)) {
                    "Unknown material '$name' in restrictions.itemFilter.materials[$index]."
                })
            }
        }

        require(!config.permissions.formats["default"].isNullOrBlank()) {
            "permissions.formats must contain a non-blank 'default' format."
        }

        require(config.dateFormat.isNotBlank()) { "dateFormat must not be blank." }
        config.dateFormatter = try {
            DateTimeFormatter.ofPattern(config.dateFormat).also { formatter ->
                LocalDateTime.now().format(formatter)
            }
        } catch (exception: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid dateFormat '${config.dateFormat}'.", exception)
        } catch (exception: DateTimeException) {
            throw IllegalArgumentException("dateFormat must support a local date and time: '${config.dateFormat}'.", exception)
        }

        restrictions.signatureFilter.compiledRegexes = restrictions.signatureFilter.regexes.mapIndexed { index, pattern ->
            try {
                Regex(pattern)
            } catch (exception: PatternSyntaxException) {
                throw IllegalArgumentException("Invalid restrictions.signatureFilter.regexes[$index]: '$pattern'.", exception)
            }
        }
    }
}
