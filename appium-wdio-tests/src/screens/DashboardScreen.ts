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
     * Botón Añadir Técnico por XPath
     */
    get submitAddTechBtn() {
        return $('//*[@text="Añadir" or contains(@text, "Añadir")]');
    }

    /**
     * Botón Listo / Cerrar Gestor por XPath
     */
    get closeTechManagerBtn() {
        return $('//*[@text="Listo" or contains(@text, "Listo")]');
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
     * Abre el Gestor de Técnicos de la cabecera, registra al técnico pulsando Añadir y cierra con Listo
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

        // 4. Clic directo al botón Añadir por XPath
        const addBtn = await $('//*[@text="Añadir" or contains(@text, "Añadir")]');
        await addBtn.waitForDisplayed({ timeout: 10000 });
        await addBtn.click();
        await driver.pause(1500);

        // 5. Clic directo al botón Listo por XPath
        const closeBtn = await $('//*[@text="Listo" or contains(@text, "Listo")]');
        await closeBtn.waitForDisplayed({ timeout: 10000 });
        await closeBtn.click();
        await driver.pause(1200);
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
            await authBtn.waitForDisplayed({ timeout: 5000 });
            await authBtn.click();
            await driver.pause(1000);
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
