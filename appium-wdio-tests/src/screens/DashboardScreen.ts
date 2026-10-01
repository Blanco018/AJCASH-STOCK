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
     * Selector de la tarjeta del vehículo de guardia (Furgoneta 1)
     */
    get van1Card() {
        return $('//*[contains(@resource-id, "vehicle_card_furgoneta_1") or contains(@resource-id, "furgoneta") or contains(@text, "Furgoneta 1")]');
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
     * Selecciona la Furgoneta 1 y confirma el técnico de guardia
     */
    async enterVan1Inventory(): Promise<void> {
        await this.van1Card.waitForDisplayed({ timeout: 10000 });
        await this.van1Card.click();

        // Espera a que aparezca el diálogo modal y confirma
        await this.submitTechAuthButton.waitForDisplayed({ timeout: 10000 });
        await this.submitTechAuthButton.click();
    }
}

export default new DashboardScreen();
