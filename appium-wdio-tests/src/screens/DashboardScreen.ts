/**
 * Page / Screen Object para la pantalla Dashboard inicial de AJCashStocks.
 * Selectores robustos compatibles con UiSelector, Resource-ID, Content-Desc y Text.
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
        return $('//*[contains(@resource-id, "header_technicians_manager_button") or contains(@text, "GESTOR TÉCNICOS")]');
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
     * Botón Añadir Técnico
     */
    get submitAddTechBtn() {
        return $('//*[contains(@resource-id, "submit_add_tech_button") or @content-desc="submit_add_tech_button" or contains(@text, "Añadir")]');
    }

    /**
     * Botón Listo / Cerrar Gestor
     */
    get closeTechManagerBtn() {
        return $('//*[contains(@resource-id, "close_technicians_manager_button") or @content-desc="close_technicians_manager_button" or contains(@text, "Listo")]');
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
     * Abre el Gestor de Técnicos de la cabecera, registra al técnico y cierra el modal
     */
    async createTechnician(name: string, number: string): Promise<void> {
        // 1. Abrir modal del gestor
        await this.headerTechManagerBtn.waitForDisplayed({ timeout: 10000 });
        await this.headerTechManagerBtn.click();
        await driver.pause(1000);

        // 2. Rellenar Nombre
        await this.newTechNameInput.waitForDisplayed({ timeout: 10000 });
        await this.newTechNameInput.setValue(name);
        await driver.pause(400);

        // 3. Rellenar Número
        await this.newTechNumberInput.waitForDisplayed({ timeout: 10000 });
        await this.newTechNumberInput.setValue(number);
        await driver.pause(400);

        // 4. Intentar enviar Enter directo en el teclado
        try {
            await driver.pressKeyCode(66); // KEYCODE_ENTER
            await driver.pause(500);
        } catch {
            // Continuar
        }

        // 5. Cerrar teclado de forma segura en Android
        try {
            if (await driver.isKeyboardShown()) {
                await driver.back();
                await driver.pause(500);
            }
        } catch {
            // Continuar
        }

        // 6. Pulsar el botón "Añadir"
        try {
            await this.submitAddTechBtn.waitForDisplayed({ timeout: 5000 });
            await this.submitAddTechBtn.click();
            await driver.pause(1000);
        } catch {
            // Ya registrado mediante Enter
        }

        // 7. Cerrar teclado por si volvió a aparecer
        try {
            if (await driver.isKeyboardShown()) {
                await driver.back();
                await driver.pause(500);
            }
        } catch {
            // Continuar
        }

        // 8. Pulsar "Listo" para confirmar y volver al Dashboard
        await this.closeTechManagerBtn.waitForDisplayed({ timeout: 10000 });
        await this.closeTechManagerBtn.click();
        await driver.pause(1200);
    }

    /**
     * Selecciona el vehículo de la flota y confirma el técnico de guardia
     */
    async enterVehicleInventory(): Promise<void> {
        await this.firstVehicleCard.waitForDisplayed({ timeout: 10000 });
        await this.firstVehicleCard.click();
        await driver.pause(1000);

        // Espera a que aparezca el diálogo modal y pulsa Acceder al Inventario
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
