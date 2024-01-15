package com.motompro.tcpconfig.app.config

import com.motompro.tcpconfig.app.TCPConfigApp
import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor
import org.yaml.snakeyaml.inspector.TagInspector
import org.yaml.snakeyaml.introspector.BeanAccess
import org.yaml.snakeyaml.introspector.Property
import org.yaml.snakeyaml.introspector.PropertyUtils
import org.yaml.snakeyaml.nodes.Tag
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.io.IOException
import java.util.Locale

private const val MAX_CONFIG_DATA_NUMBER = 7

/**
 * This class manages configs operations such as loading or deleting
 */
class ConfigManager {

    private val _configs = mutableMapOf<String, Pair<Config, File>>()
    val configs: Map<String, Config>
        get() = _configs.mapValues { it.value.first }

    private val configsDirectory = File(File(TCPConfigApp::class.java.protectionDomain.codeSource.location.path.replace("%20", " ")).parentFile, "configs")
    private val yamlLoader: Yaml

    init {
        val loaderOptions = LoaderOptions()
        loaderOptions.tagInspector = TagInspector { tag: Tag -> tag.className == Config::class.java.name }
        val constructor = CustomClassLoaderConstructor(javaClass.classLoader, loaderOptions)
        constructor.propertyUtils = PROPERTY_UTILS
        yamlLoader = Yaml(constructor)
        yamlLoader.setBeanAccess(BeanAccess.FIELD)
    }

    /**
     * Load configs from the default configs directory
     */
    fun loadConfigs() {
        if (!configsDirectory.exists()) {
            configsDirectory.mkdirs()
            return
        }
        if (!configsDirectory.isDirectory) {
            throw IllegalStateException()
        }
        configsDirectory.listFiles()
            ?.filter { it.extension == CONFIG_FILE_EXTENSION || it.extension == LEGACY_CONFIG_FILE_EXTENSION }
            ?.forEach { configFile -> loadConfig(configFile) }
    }

    /**
     * Load config from a given config file. If the file is recognized as a legacy one then it will be converted in a
     * correct one
     * @param configFile the config file
     * @return the loaded config data
     */
    fun loadConfig(configFile: File): Config {
        if (configFile.extension == LEGACY_CONFIG_FILE_EXTENSION) {
            val lines = configFile.readLines()
            if (lines.size < 4) {
                throw IllegalArgumentException()
            }
            val config = createConfigFromDataList(lines)
            val destinationFile = File(configsDirectory, "${config.name}.yml")
            _configs[config.name] = config to destinationFile
            try {
                saveConfig(config, destinationFile)
                configFile.delete()
            } catch (ex: IOException) {
                TCPConfigApp.INSTANCE.showErrorAlert("Erreur", ex.stackTraceToString())
            }
            return config
        }
        if (configFile.extension == LEGACY_SAVE_FILE_EXTENSION) {
            val lines = configFile.readLines()
            if (lines.size < MAX_CONFIG_DATA_NUMBER) {
                throw IllegalArgumentException()
            }
            var configLineNumber = 0
            var firstConfig: Config? = null
            while (configLineNumber + MAX_CONFIG_DATA_NUMBER <= lines.size) {
                val dataLines = mutableListOf<String>()
                for (i in 0 until MAX_CONFIG_DATA_NUMBER) {
                    if (lines[configLineNumber + i].isNotBlank()) dataLines.add(lines[configLineNumber + i])
                }
                val config = createConfigFromDataList(dataLines)
                if (firstConfig == null) firstConfig = config
                val destinationFile = File(configsDirectory, "${config.name}.yml")
                _configs[config.name] = config to destinationFile
                try {
                    saveConfig(config, destinationFile)
                } catch (ex: IOException) {
                    TCPConfigApp.INSTANCE.showErrorAlert("Erreur", ex.stackTraceToString())
                }
                configLineNumber += MAX_CONFIG_DATA_NUMBER
            }
            return firstConfig!!
        }
        if (configFile.extension != CONFIG_FILE_EXTENSION) {
            throw IllegalArgumentException()
        }
        val inputStream = FileInputStream(configFile)
        val config = yamlLoader.loadAs(inputStream, Config::class.java)
        val name = findNotUsedName(config.name)
        config.name = name
        _configs[name] = config to configFile
        inputStream.close()
        return config
    }

    private fun createConfigFromDataList(dataList: List<String>): Config {
        val name = findNotUsedName(dataList[0])
        return Config(
                name,
                dataList[1],
                dataList[2],
                dataList[3],
                if (dataList.size >= 5) dataList[4] else null,
                if (dataList.size >= 6) dataList[5] else null,
                if (dataList.size >= 7) dataList[6] else null,
        )
    }

    /**
     * Save a config in the given destination file
     * @param config the config's data
     * @param destination *optional* - the destination file
     */
    fun saveConfig(config: Config, destination: File = File(configsDirectory, "${config.name}.yml")) {
        destination.createNewFile()
        val fileWriter = FileWriter(destination)
        yamlLoader.dump(config, fileWriter)
        fileWriter.close()
    }

    /**
     * Add a config to the map and to the files and rename it if another config with the same name already exists
     * @param config the config to add
     */
    fun addConfig(config: Config) {
        val finalName = findNotUsedName(config.name)
        config.name = finalName
        val configFile = File(configsDirectory, "$finalName.yml")
        saveConfig(config, configFile)
        _configs[finalName] = config to configFile
    }

    /**
     * Add a (i) after the given name if it already exists
     * @param wantedName the original name
     * @return the original name with an appending if needed
     */
    private fun findNotUsedName(wantedName: String): String {
        if (!_configs.containsKey(wantedName)) return wantedName
        var i = 2
        var name: String
        do {
            name = "$wantedName ($i)"
            i++
        } while (_configs.containsKey(name))
        return name
    }

    /**
     * Remove a config from the list and from the config files
     * @param config the config to remove
     */
    fun removeConfig(config: Config) {
        _configs[config.name]?.second?.delete()
        _configs.remove(config.name)
    }

    companion object {
        const val CONFIG_FILE_EXTENSION = "yml"
        const val LEGACY_CONFIG_FILE_EXTENSION = "tcpc"
        const val LEGACY_SAVE_FILE_EXTENSION = "txt"

        /**
         * Used to convert properties name that contain dashes to camel case
         */
        private val PROPERTY_UTILS = object : PropertyUtils() {
            override fun getProperty(type: Class<out Any>?, name: String?): Property {
                val formattedName = if (name?.contains('-') == true) {
                    formatToCamelCase(name)
                } else {
                    name
                }
                return super.getProperty(type, formattedName)
            }
        }

        private fun formatToCamelCase(str: String): String {
            val formattedString = str.split('-')
                .filter { it.isNotBlank() }
                .joinToString("") { "${it[0].uppercase(Locale.ENGLISH)}${it.substring(1)}" }
            return "${formattedString[0].lowercase(Locale.ENGLISH)}${formattedString.substring(1)}"
        }
    }
}
