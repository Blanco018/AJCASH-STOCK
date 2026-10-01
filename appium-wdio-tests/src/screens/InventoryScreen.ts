/**
 * Page / Screen Object para la pantalla de Inventario de Vehículo en AJCashStocks.
 * Selectores robustos mediante XPath, Resource-ID y UiSelector.
 */
export class InventoryScreen {
    /**
     * Contenedor de la pantalla de inventario por Resource ID o XPath
     */
    get inventoryRoot() {
        return $('//*[contains(@resource-id, "vehicle_inventory_screen") or @content-desc="vehicle_inventory_screen"]');
    }

    /**
     * Campo de entrada de texto para búsqueda de repuestos
     */
    get searchInput() {
        return $('//*[contains(@resource-id, "search_input") or @content-desc="search_input" or @className="android.widget.EditText"]');
    }

    /**
     * Botón de navegación Atrás
     */
    get backButton() {
        return $('//*[contains(@resource-id, "back_button") or @content-desc="Volver al listado de vehículos" or contains(@content-desc, "Volver")]');
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
        try {
            await this.inventoryRoot.waitForDisplayed({ timeout: 8000 });
            return true;
        } catch {
            const searchOrTitle = await $('android=new UiSelector().className("android.widget.EditText")');
            return searchOrTitle.isDisplayed();
        }
    }

    /**
     * Escribe un término en la barra de búsqueda y espera la reacción de Compose
     */
    async searchProduct(query: string): Promise<void> {
        try {
            await this.searchInput.waitForDisplayed({ timeout: 5000 });
        } catch {
            try {
                await $('android=new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().className("android.widget.EditText"))');
            } catch {}
        }
        await this.searchInput.waitForDisplayed({ timeout: 10000 });
        await this.searchInput.setValue(query);
        await driver.pause(1000);
    }

    /**
     * Pulsa el botón Atrás para retornar al Dashboard
     */
    async clickBack(): Promise<void> {
        try {
            await this.backButton.waitForDisplayed({ timeout: 5000 });
            await this.backButton.click();
        } catch {
            await driver.back();
        }
        await driver.pause(1000);
    }
}

export default new InventoryScreen();
