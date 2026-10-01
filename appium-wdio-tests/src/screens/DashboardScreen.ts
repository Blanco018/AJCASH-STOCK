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
