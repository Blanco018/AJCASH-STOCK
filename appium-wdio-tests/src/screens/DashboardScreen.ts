/**
 * Page / Screen Object para la pantalla Dashboard inicial de AJCashStocks.
 * Selectores directos mediante XPath y UiSelector nativo.
 */
export class DashboardScreen {
    /**
     * Selector del contenedor principal del Dashboard
     */
    get dashboardRoot() {
        return $('//*[contains(@resource-id, "dashboard_screen") or @content-desc="dashboard_screen"]');
    }

    /**
     * Botón Gestor de Técnicos en la cabecera
     */
    get headerTechManagerBtn() {
        return $('//*[@text="GESTOR TÉCNICOS" or contains(@resource-id, "header_technicians_manager_button")]');
    }

    /**
     * Input de Nombre y Apellidos (primer campo de texto dentro del modal)
     */
    get newTechNameInput() {
        return $('android=new UiSelector().className("android.widget.EditText").instance(0)');
    }

    /**
     * Input de Número de técnico (segundo campo de texto dentro del modal)
     */
    get newTechNumberInput() {
        return $('android=new UiSelector().className("android.widget.EditText").instance(1)');
    }

    /**
     * Botón de guardado y cierre del Gestor
     */
    get submitAddTechBtn() {
        return $('//*[contains(@text, "Añadir") or contains(@text, "Listo")]');
    }

    /**
     * Selector de la tarjeta del vehículo de la flota
     */
    get firstVehicleCard() {
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
     * Abre el Gestor de Técnicos de la cabecera, registra al técnico y cierra
     */
    async createTechnician(name: string, number: string): Promise<void> {
        // 1. Abrir modal del gestor
        const openManagerBtn = await $('//*[@text="GESTOR TÉCNICOS" or contains(@resource-id, "header_technicians_manager_button")]');
        await openManagerBtn.waitForDisplayed({ timeout: 10000 });
        await openManagerBtn.click();
        await driver.pause(1000);

        // 2. Rellenar Nombre
        const nameField = await $('android=new UiSelector().className("android.widget.EditText").instance(0)');
        await nameField.waitForDisplayed({ timeout: 10000 });
        await nameField.setValue(name);
        await driver.pause(400);

        // 3. Rellenar Número
        const numberField = await $('android=new UiSelector().className("android.widget.EditText").instance(1)');
        await numberField.waitForDisplayed({ timeout: 10000 });
        await numberField.setValue(number);
        await driver.pause(400);

        // 4. Intentar acción de guardado mediante ENTER nativo
        try {
            await driver.pressKeyCode(66);
            await driver.pause(500);
        } catch {}

        // 5. Clic al botón principal de confirmación del diálogo
        try {
            const submitBtn = await $('//*[contains(@text, "Añadir") or contains(@text, "Listo")]');
            if (await submitBtn.isDisplayed()) {
                await submitBtn.click();
                await driver.pause(800);
            }
        } catch {}

        // 6. Salvaguarda: si por alguna razón el diálogo sigue en pantalla, pulsar atrás para cerrarlo
        try {
            const modalHeader = await $('//*[@text="GESTOR DE TÉCNICOS"]');
            if (await modalHeader.isDisplayed()) {
                await driver.back();
                await driver.pause(600);
            }
        } catch {}

        await driver.pause(1000);
    }

    /**
     * Selecciona el vehículo de la flota y confirma el técnico de guardia
     */
    async enterVehicleInventory(): Promise<void> {
        await this.firstVehicleCard.waitForDisplayed({ timeout: 10000 });
        await this.firstVehicleCard.click();
        await driver.pause(1200);

        // Si aparece el diálogo modal de confirmación, pulsar Acceder al Inventario
        try {
            const authBtn = await $('//*[contains(@resource-id, "submit_tech_auth_button") or contains(@text, "Acceder al Inventario")]');
            if (await authBtn.isDisplayed()) {
                await authBtn.click();
                await driver.pause(1000);
            }
        } catch {
            // Ya accedió directamente al inventario
        }
    }

    /**
     * Alias para compatibilidad con suites
     */
    async enterVan1Inventory(): Promise<void> {
        return this.enterVehicleInventory();
    }
}

export default new DashboardScreen();
