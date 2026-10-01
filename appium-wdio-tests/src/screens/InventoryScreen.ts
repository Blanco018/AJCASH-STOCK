/**
 * Page / Screen Object para la pantalla de Inventario de Vehículo en AJCashStocks.
 * Utiliza selectores directos por Resource-ID de Android / UiSelector para máxima velocidad y certeza.
 */
export class InventoryScreen {
    /**
     * Contenedor de la pantalla de inventario por Resource ID
     */
    get inventoryRoot() {
        return $('android=new UiSelector().resourceIdMatches(".*vehicle_inventory_screen.*")');
    }

    /**
     * Campo de entrada de texto para búsqueda de repuestos por Resource ID
     */
    get searchInput() {
        return $('android=new UiSelector().resourceIdMatches(".*search_input.*")');
    }

    /**
     * Botón de navegación Atrás por Resource ID
     */
    get backButton() {
        return $('android=new UiSelector().resourceIdMatches(".*back_button.*")');
    }

    /**
     * Localizador dinámico por texto de producto
     */
    getProductNode(productName: string) {
        return $(`android=new UiSelector().textContains("${productName}")`);
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
        try {
            await this.searchInput.waitForDisplayed({ timeout: 5000 });
        } catch {
            try {
                // Scroll asistido por UiScrollable si la caja estuviera bajo el pliegue
                await $('android=new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().resourceIdMatches(".*search_input.*"))');
            } catch {
                // Continuar
            }
        }
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
        await driver.pause(1000);
    }
}

export default new InventoryScreen();
