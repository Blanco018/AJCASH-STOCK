import DashboardScreen from '../screens/DashboardScreen';
import InventoryScreen from '../screens/InventoryScreen';

describe('AJCashStocks - Mobile Automation Suite (Appium + WebdriverIO)', () => {

    it('TC01: Debe iniciar la aplicación y desplegar el Dashboard corporativo', async () => {
        const isDashboardVisible = await DashboardScreen.isDisplayed();
        expect(isDashboardVisible).toBe(true);
    });

    it('TC02: Debe ingresar al inventario de la Furgoneta 1 tras autorizar el técnico', async () => {
        // Seleccionar Furgoneta 1 e ingresar
        await DashboardScreen.enterVan1Inventory();

        // Validar que la vista de inventario del vehículo está activa
        const isInventoryVisible = await InventoryScreen.isDisplayed();
        expect(isInventoryVisible).toBe(true);
    });

    it('TC03: Debe filtrar el catálogo de repuestos al buscar "TPV"', async () => {
        // Buscar el término TPV
        await InventoryScreen.searchProduct('TPV');

        // Validar que el resultado filtrado aparece en la pantalla
        const tpvItem = InventoryScreen.getProductNode('TPV');
        await tpvItem.waitForDisplayed({ timeout: 8000 });
        const isTpvDisplayed = await tpvItem.isDisplayed();
        expect(isTpvDisplayed).toBe(true);
    });

    it('TC04: Debe volver al Dashboard principal al pulsar el botón Atrás', async () => {
        // Presionar botón Atrás
        await InventoryScreen.clickBack();

        // Verificar que estamos de nuevo en el Dashboard
        const isDashboardBack = await DashboardScreen.isDisplayed();
        expect(isDashboardBack).toBe(true);
    });
});
