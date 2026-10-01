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
     * Selector de la tarjeta del vehículo por Resource ID
     */
    get firstVehicleCard() {
        return $('android=new UiSelector().resourceIdMatches(".*vehicle_card_.*")');
    }

    /**
     * Botón de confirmación / asignación del Técnico de Guardia en el diálogo por Resource ID
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
     * Selecciona el vehículo visible y confirma el técnico de guardia
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
     * Alias para compatibilidad con suites existentes
     */
    async enterVan1Inventory(): Promise<void> {
        return this.enterVehicleInventory();
    }
}

export default new DashboardScreen();
