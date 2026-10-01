/**
 * Page / Screen Object para la pantalla Dashboard inicial de AJCashStocks.
 * Utiliza selectores directos por Resource-ID de Android / UiSelector para máxima velocidad y certeza.
 */
export class DashboardScreen {
    /**
     * Selector del contenedor principal del Dashboard por Resource ID
     */
    get dashboardRoot() {
        return $('android=new UiSelector().resourceIdMatches(".*dashboard_screen.*")');
    }

    /**
     * Botón Gestor de Técnicos en la cabecera
     */
    get headerTechManagerBtn() {
        return $('android=new UiSelector().resourceIdMatches(".*header_technicians_manager_button.*")');
    }

    /**
     * Input de Nombre y Apellidos (primer campo de texto dentro del gestor)
     */
    get newTechNameInput() {
        return $('android=new UiSelector().className("android.widget.EditText").instance(0)');
    }

    /**
     * Input de Número de técnico (segundo campo de texto dentro del gestor)
     */
    get newTechNumberInput() {
        return $('android=new UiSelector().className("android.widget.EditText").instance(1)');
    }

    /**
     * Botón Añadir Técnico
     */
    get submitAddTechBtn() {
        return $('android=new UiSelector().textContains("Añadir")');
    }

    /**
     * Botón Listo / Cerrar Gestor
     */
    get closeTechManagerBtn() {
        return $('android=new UiSelector().textContains("Listo")');
    }

    /**
     * Selector de la tarjeta del vehículo de la flota
     */
    get firstVehicleCard() {
        return $('android=new UiSelector().resourceIdMatches(".*vehicle_card_.*")');
    }

    /**
     * Botón de confirmación / asignación del Técnico de Guardia en el diálogo
     */
    get submitTechAuthButton() {
        return $('android=new UiSelector().resourceIdMatches(".*submit_tech_auth_button.*")');
    }

    /**
     * Verifica que el Dashboard esté visible
     */
    async isDisplayed(): Promise<boolean> {
        await this.dashboardRoot.waitForDisplayed({ timeout: 15000 });
        return this.dashboardRoot.isDisplayed();
    }

    /**
     * Abre el Gestor de Técnicos de la cabecera, registra al técnico y cierra el modal
     */
    async createTechnician(name: string, number: string): Promise<void> {
        await this.headerTechManagerBtn.waitForDisplayed({ timeout: 10000 });
        await this.headerTechManagerBtn.click();
        await driver.pause(1000);

        // Rellenar Nombre y Apellidos
        await this.newTechNameInput.waitForDisplayed({ timeout: 10000 });
        await this.newTechNameInput.setValue(name);
        await driver.pause(500);

        // Rellenar Nº de Técnico
        await this.newTechNumberInput.waitForDisplayed({ timeout: 10000 });
        await this.newTechNumberInput.setValue(number);
        await driver.pause(500);

        // Pulsar Añadir
        await this.submitAddTechBtn.waitForDisplayed({ timeout: 10000 });
        await this.submitAddTechBtn.click();
        await driver.pause(1000);

        // Cerrar Gestor con Listo
        await this.closeTechManagerBtn.waitForDisplayed({ timeout: 10000 });
        await this.closeTechManagerBtn.click();
        await driver.pause(1000);
    }

    /**
     * Selecciona el vehículo de la flota y confirma el técnico de guardia
     */
    async enterVehicleInventory(): Promise<void> {
        await this.firstVehicleCard.waitForDisplayed({ timeout: 10000 });
        await this.firstVehicleCard.click();
        await driver.pause(800);

        // Espera a que aparezca el diálogo modal y confirma
        await this.submitTechAuthButton.waitForDisplayed({ timeout: 10000 });
        await this.submitTechAuthButton.click();
        await driver.pause(1000);
    }

    /**
     * Alias para compatibilidad con suites
     */
    async enterVan1Inventory(): Promise<void> {
        return this.enterVehicleInventory();
    }
}

export default new DashboardScreen();
