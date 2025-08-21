/**
 * Main Application JavaScript
 * Initializes and manages all modules
 */
class App {
    constructor() {
        this.modules = {};
        this.init();
    }

    init() {
        this.loadModules();
        this.setupGlobalEventListeners();
    }

    loadModules() {
        // Initialize core modules
        this.modules.toast = new ToastManager();
        this.modules.validator = new FormValidator();
        this.modules.auth = new Authentication();
        
        console.log('All modules loaded successfully');
    }

    setupGlobalEventListeners() {
        // Global error handler
        window.addEventListener('error', (e) => {
            console.error('Global error:', e.error);
            this.modules.toast.showToast('danger', 'An unexpected error occurred');
        });

        // Global unhandled promise rejection handler
        window.addEventListener('unhandledrejection', (e) => {
            console.error('Unhandled promise rejection:', e.reason);
            this.modules.toast.showToast('danger', 'An unexpected error occurred');
        });

        // Global AJAX error handler
        $(document).ajaxError((event, xhr, settings, error) => {
            console.error('AJAX error:', error);
            this.modules.toast.showToast('danger', 'Request failed. Please try again.');
        });
    }

    // Utility method to show toast from anywhere
    showToast(status, message) {
        if (this.modules.toast) {
            this.modules.toast.showToast(status, message);
        }
    }

    // Utility method to validate form
    validateForm(form) {
        if (this.modules.validator) {
            return this.modules.validator.validateForm(form);
        }
        return true;
    }
}

// Initialize app when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    window.app = new App();
}); 