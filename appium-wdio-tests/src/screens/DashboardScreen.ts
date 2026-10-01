/**
 * Page / Screen Object para la pantalla Dashboard inicial de AJCashStocks.
 */
export class DashboardScreen {
    /**
     * Selector del contenedor principal del Dashboard
     */
    get dashboardRoot() {
        return $('//*[@resource-id="dashboard_screen" or @content-desc="dashboard_screen"]');
    }

    /**
     * Selector de la tarjeta del vehículo de guardia (Coche 1 o Furgoneta 1)
     */
    get van1Card() {
        return $('//*[contains(@resource-id, "vehicle_card_") or contains(@text, "Coche 1") or contains(@text, "Furgoneta 1")]');
    }

    /**
     * Botón de confirmación / asignación del Técnico de Guardia en el diálogo
     */
    get submitTechAuthButton() {
        return $('//*[contains(@resource-id, "submit_tech_auth_button") or contains(@text, "Acceder al Inventario")]');
    }

    /**
     * Verifica que el Dashboard esté visible
     */
    async isDisplayed(): Promise<boolean> {
        await this.dashboardRoot.waitForDisplayed({ timeout: 15000 });
        return this.dashboardRoot.isDisplayed();
    }

    /**
     * Botón Gestor de Técnicos en la cabecera
     */
    get headerTechManagerBtn() {
        return $('//*[contains(@resource-id, "header_technicians_manager_button") or contains(@text, "GESTOR TÉCNICOS")]');
    }

    /**
     * Input de Nombre y Apellidos del nuevo técnico
     */
    get newTechNameInput() {
        return $('//*[contains(@resource-id, "new_tech_name_input")]');
    }

    /**
     * Input de Número de técnico
     */
    get newTechNumberInput() {
        return $('//*[contains(@resource-id, "new_tech_number_input")]');
    }

    /**
     * Botón Añadir Técnico
     */
    get submitAddTechBtn() {
        return $('//*[contains(@resource-id, "submit_add_tech_button") or contains(@text, "Añadir")]');
    }

    /**
     * Botón Listo / Cerrar Gestor
     */
    get closeTechManagerBtn() {
        return $('//*[contains(@resource-id, "close_technicians_manager_button") or contains(@text, "Listo")]');
    }

    /**
     * Da de alta a un técnico desde el Gestor de Técnicos de la cabecera
     */
    async createTechnician(name: string, number: string): Promise<void> {
        await this.headerTechManagerBtn.waitForDisplayed({ timeout: 10000 });
        await this.headerTechManagerBtn.click();
        await driver.pause(600);

        await this.newTechNameInput.waitForDisplayed({ timeout: 10000 });
        await this.newTechNameInput.setValue(name);

        await this.newTechNumberInput.waitForDisplayed({ timeout: 10000 });
        await this.newTechNumberInput.setValue(number);

        await this.submitAddTechBtn.waitForDisplayed({ timeout: 10000 });
        await this.submitAddTechBtn.click();
        await driver.pause(800);

        await this.closeTechManagerBtn.waitForDisplayed({ timeout: 10000 });
        await this.closeTechManagerBtn.click();
        await driver.pause(800);
    }

    /**
     * Selecciona el primer vehículo visible y confirma el técnico de guardia
     */
    async enterVan1Inventory(): Promise<void> {
        await this.van1Card.waitForDisplayed({ timeout: 10000 });
        await this.van1Card.click();
        await driver.pause(800);

        // Espera a que aparezca el diálogo modal y confirma
        await this.submitTechAuthButton.waitForDisplayed({ timeout: 10000 });
        await this.submitTechAuthButton.click();
        await driver.pause(1000);
    }
}

export default new DashboardScreen();
