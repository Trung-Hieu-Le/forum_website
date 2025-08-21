/**
 * Toast Notification Module
 * Handles all toast notifications and displays
 */
class ToastManager {
    constructor() {
        this.init();
    }

    init() {
        this.createToastContainer();
        this.bindEvents();
    }

    createToastContainer() {
        // Create toast container if it doesn't exist
        if (!document.querySelector('.toast-container')) {
            const container = document.createElement('div');
            container.className = 'toast-container position-fixed top-0 end-0 p-3';
            container.style.cssText = 'z-index: 9999;';
            document.body.appendChild(container);
        }
    }

    bindEvents() {
        // Listen for custom showToast events
        document.addEventListener('showToast', (e) => {
            this.showToast(e.detail.status, e.detail.message);
        });
    }

    showToast(status, message) {
        const toastStyles = {
            primary: { headerBg: '#cfe2ff', color: '#0d6efd' },
            success: { headerBg: '#d1e7dd', color: '#198754' },
            warning: { headerBg: '#fff3cd', color: '#ffc107' },
            danger: { headerBg: '#f8d7da', color: '#dc3545' }
        };

        const icons = {
            primary: 'fa-info-circle',
            success: 'fa-check-circle',
            warning: 'fa-exclamation-triangle',
            danger: 'fa-times-circle'
        };

        const isDanger = status === 'danger';
        const toastDelay = 10000;
        const toastId = `toast-${Date.now()}`;

        const toastHtml = `
            <div id="${toastId}" class="toast toast-${status}" role="alert" aria-live="assertive" aria-atomic="true"
                style="background-color: ${toastStyles[status].headerBg};"
                data-bs-autohide="${!isDanger}" ${!isDanger ? `data-bs-delay="${toastDelay}"` : ''}>
                <div class="toast-header" style="background-color: ${toastStyles[status].headerBg}; color: ${toastStyles[status].color}">
                    <i class="fas ${icons[status]} me-2" style="color: ${toastStyles[status].color}"></i>
                    <strong class="me-auto">${status.charAt(0).toUpperCase() + status.slice(1)}</strong>
                    <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
                    <div class="progress w-100 mt-2">
                        <div class="progress-bar" style="width: 100%; background-color: ${toastStyles[status].color}; height: 4px;" role="progressbar"></div>
                    </div>
                </div>
                <div class="toast-body">${message}</div>
            </div>
        `;

        const container = document.querySelector('.toast-container');
        container.insertAdjacentHTML('beforeend', toastHtml);
        
        const toastElement = document.getElementById(toastId);
        const toast = new bootstrap.Toast(toastElement);
        toast.show();

        if (!isDanger) {
            setTimeout(() => {
                const progressBar = toastElement.querySelector('.progress-bar');
                if (progressBar) {
                    progressBar.style.width = '0%';
                    progressBar.style.transition = `width ${toastDelay}ms linear`;
                }
            }, 10);
        }

        // Remove toast element after it's hidden
        toastElement.addEventListener('hidden.bs.toast', () => {
            toastElement.remove();
        });
    }

    // Method to show multiple toasts for array of messages
    showMultipleToasts(status, messages) {
        if (Array.isArray(messages)) {
            messages.forEach(message => this.showToast(status, message));
        }
    }
}

// Initialize toast manager when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new ToastManager();
}); 