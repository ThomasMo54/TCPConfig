package com.motompro.tcpconfig.app.controller

import com.motompro.tcpconfig.app.TCPConfigApp
import com.motompro.tcpconfig.app.config.Config
import com.motompro.tcpconfig.app.exception.InvalidConfigFieldException
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField

private const val CONFIG_NAME_MAX_LENGTH = 50
private val IP_ADDRESS_REGEX = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}\$".toRegex()

class AddEditConfigController {

    var configInEdition: Config? = null
        set(value) {
            field = value
            setExistingFields()
        }

    lateinit var backButton: Button
    lateinit var nameTextField: TextField
    lateinit var netInterfaceComboBox: ComboBox<String>
    lateinit var ipTextField: TextField
    lateinit var maskTextField: TextField
    lateinit var gatewayTextField: TextField
    lateinit var favDNSTextField: TextField
    lateinit var auxDNSTextField: TextField

    @FXML
    private fun initialize() {
        MainController.addDarkenEffect(backButton)
        val netInterfaces = TCPConfigApp.INSTANCE.netInterfaceManager.netInterfaces.sorted()
        netInterfaceComboBox.items.addAll(netInterfaces)
        netInterfaceComboBox.selectionModel.selectFirst()
    }

    @FXML
    private fun onBackButtonClick(event: ActionEvent) {
        TCPConfigApp.INSTANCE.swapScene("main-view.fxml")
    }

    @FXML
    private fun onValidateButtonClick(event: ActionEvent) {
        try {
            validateFields()
            if (configInEdition != null) {
                TCPConfigApp.INSTANCE.configManager.removeConfig(configInEdition!!)
                configInEdition!!.name = nameTextField.text
                configInEdition!!.networkAdapter = netInterfaceComboBox.value
                configInEdition!!.ip = ipTextField.text
                configInEdition!!.subnetMask = maskTextField.text
                configInEdition!!.defaultGateway = gatewayTextField.text.ifBlank { null }
                configInEdition!!.preferredDNS = favDNSTextField.text.ifBlank { null }
                configInEdition!!.auxDNS = auxDNSTextField.text.ifBlank { null }
                TCPConfigApp.INSTANCE.configManager.addConfig(configInEdition!!)
            } else {
                val config = Config(
                    nameTextField.text,
                    netInterfaceComboBox.value,
                    ipTextField.text,
                    maskTextField.text,
                    gatewayTextField.text.ifBlank { null },
                    favDNSTextField.text.ifBlank { null },
                    auxDNSTextField.text.ifBlank { null },
                )
                TCPConfigApp.INSTANCE.configManager.addConfig(config)
            }
            val controller = TCPConfigApp.INSTANCE.swapScene("main-view.fxml", MainController::class.java)
            controller.updateConfigList()
        } catch (ex: InvalidConfigFieldException) {
            when (ex.type) {
                InvalidConfigFieldException.Type.NAME_TOO_LONG -> {
                    TCPConfigApp.INSTANCE.showErrorAlert("Erreur", "Le nom doit avoir une longueur de $CONFIG_NAME_MAX_LENGTH caractères max.")
                }
                InvalidConfigFieldException.Type.BAD_IP_FORMAT -> {
                    TCPConfigApp.INSTANCE.showErrorAlert("Erreur", "Le champ \"${ex.fieldName}\" contient une adresse IP mal formatée")
                }
                InvalidConfigFieldException.Type.MISSING_DNS_FIELD -> {
                    TCPConfigApp.INSTANCE.showErrorAlert("Erreur", "Les 2 champs DNS doivent être remplis")
                }
                InvalidConfigFieldException.Type.MISSING_MANDATORY_FIELD -> {
                    TCPConfigApp.INSTANCE.showErrorAlert("Erreur", "Le champ \"${ex.fieldName}\" est obligatoire")
                }
            }
        }
    }

    private fun setExistingFields() {
        configInEdition!!.let { config ->
            nameTextField.text = config.name
            netInterfaceComboBox.selectionModel.select(config.networkAdapter)
            ipTextField.text = config.ip
            maskTextField.text = config.subnetMask
            if (config.defaultGateway != null) gatewayTextField.text = config.defaultGateway
            if (config.preferredDNS != null) favDNSTextField.text = config.preferredDNS
            if (config.auxDNS != null) auxDNSTextField.text = config.auxDNS
        }
    }

    private fun validateFields() {
        val mandatoryFields = mapOf(
            nameTextField to "Nom",
            ipTextField to "Adresse IP",
            maskTextField to "Masque de sous-réseau",
        )
        mandatoryFields.filter { it.key.text.isBlank() }.forEach {
            throw InvalidConfigFieldException(InvalidConfigFieldException.Type.MISSING_MANDATORY_FIELD, it.value)
        }
        if (nameTextField.text.length > CONFIG_NAME_MAX_LENGTH) {
            throw InvalidConfigFieldException(InvalidConfigFieldException.Type.NAME_TOO_LONG)
        }
        val ipFields = mapOf(
            ipTextField to "Adresse IP",
            maskTextField to "Masque de sous-réseau",
            gatewayTextField to "Passerelle par défaut",
            favDNSTextField to "DNS Favori",
            auxDNSTextField to "DNS Auxiliaire",
        )
        ipFields.filter { it.key.text.isNotBlank() }.forEach {
            if (!IP_ADDRESS_REGEX.matches(it.key.text)) throw InvalidConfigFieldException(InvalidConfigFieldException.Type.BAD_IP_FORMAT, it.value)
        }
        if ((favDNSTextField.text.isNotBlank() && auxDNSTextField.text.isBlank()) || favDNSTextField.text.isBlank() && auxDNSTextField.text.isNotBlank()) {
            throw InvalidConfigFieldException(InvalidConfigFieldException.Type.MISSING_DNS_FIELD)
        }
    }
}
