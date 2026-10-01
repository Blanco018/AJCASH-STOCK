/**
 * Page / Screen Object para la pantalla de Inventario de Vehículo en AJCashStocks.
 */
export class InventoryScreen {
    /**
     * Contenedor de la pantalla de inventario
     */
    get inventoryRoot() {
        return $('//*[@resource-id="vehicle_inventory_screen" or @content-desc="vehicle_inventory_screen"]');
    }

    /**
     * Campo de entrada de texto para búsqueda de repuestos
     */
    get searchInput() {
        return $('//*[@resource-id="search_input" or @content-desc="search_input"]');
    }

    /**
     * Botón de navegación Atrás
     */
    get backButton() {
        return $('//*[@resource-id="back_button" or @content-desc="back_button"]');
    }

    /**
     * Localizador dinámico por texto de producto
     */
    getProductNode(productName: string) {
        return $(`//*[contains(@text, "${productName}")]`);
    }

    /**
     * Valida que la pantalla de inventario esté desplegada
     */
    async isDisplayed(): Promise<boolean> {
        await this.inventoryRoot.waitForDisplayed({ timeout: 15000 });
        return this.inventoryRoot.isDisplayed();
    }

    /**
     * Escribe un término en la barra de búsqueda y espera la reacción de Compose
     */
    async searchProduct(query: string): Promise<void> {
        await this.searchInput.waitForDisplayed({ timeout: 10000 });
        await this.searchInput.setValue(query);
        await driver.pause(1000); // Pausa de estabilización reactiva
    }

    /**
     * Pulsa el botón Atrás para retornar al Dashboard
     */
    async clickBack(): Promise<void> {
        await this.backButton.waitForDisplayed({ timeout: 10000 });
        await this.backButton.click();
    }
}

export default new InventoryScreen();
