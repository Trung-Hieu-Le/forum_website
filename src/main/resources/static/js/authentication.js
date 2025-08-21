/**
 * Authentication Module
 * Handles all authentication-related functionality
 */
class Authentication {
    constructor() {
        this.init();
    }

    init() {
        this.bindEvents();
    }

    bindEvents() {
        // Login form
        const loginForm = document.getElementById('loginForm');
        if (loginForm) {
            loginForm.addEventListener('submit', (e) => this.handleLogin(e));
        }

        // Register form
        const registerForm = document.getElementById('registerForm');
        if (registerForm) {
            registerForm.addEventListener('submit', (e) => this.handleRegister(e));
        }

        // Forgot password form
        const forgotPasswordForm = document.getElementById('forgotPasswordForm');
        if (forgotPasswordForm) {
            forgotPasswordForm.addEventListener('submit', (e) => this.handleForgotPassword(e));
        }

        // Reset password form
        const resetPasswordForm = document.getElementById('resetPasswordForm');
        if (resetPasswordForm) {
            resetPasswordForm.addEventListener('submit', (e) => this.handleResetPassword(e));
        }
    }

    async handleLogin(e) {
        e.preventDefault();
        const form = e.target;
        const formData = this.getFormData(form);
        
        try {
            const response = await this.makeRequest('/login', 'POST', formData);
            this.handleApiResponse(response);
        } catch (error) {
            this.showToast('danger', 'An error occurred during login');
        }
    }

    async handleRegister(e) {
        e.preventDefault();
        const form = e.target;
        const formData = this.getFormData(form);
        
        try {
            const response = await this.makeRequest('/register', 'POST', formData);
            this.handleApiResponse(response);
        } catch (error) {
            this.showToast('danger', 'An error occurred during registration');
        }
    }

    async handleForgotPassword(e) {
        e.preventDefault();
        const form = e.target;
        const formData = this.getFormData(form);
        
        try {
            const response = await this.makeRequest('/forgot-password', 'POST', formData);
            this.handleApiResponse(response);
        } catch (error) {
            this.showToast('danger', 'An error occurred while processing your request');
        }
    }

    async handleResetPassword(e) {
        e.preventDefault();
        const form = e.target;
        const formData = this.getFormData(form);
        
        try {
            const response = await this.makeRequest('/reset-password', 'POST', formData);
            this.handleApiResponse(response);
        } catch (error) {
            this.showToast('danger', 'An error occurred while resetting password');
        }
    }

    getFormData(form) {
        const formData = {};
        const inputs = form.querySelectorAll('input, select, textarea');
        
        inputs.forEach(input => {
            if (input.name && input.value !== undefined) {
                formData[input.name] = input.value;
            }
        });
        
        return formData;
    }

    async makeRequest(url, method, data) {
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
                'X-Requested-With': 'XMLHttpRequest'
            },
            body: JSON.stringify(data)
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    }

    handleApiResponse(response) {
        if (response.type === 'toast') {
            this.showToast(response.errorTag, response.message);
        }

        if (response.status === 'ok' && response.redirectUrl) {
            setTimeout(() => {
                window.location.href = response.redirectUrl;
            }, 800);
        }
    }

    showToast(status, message) {
        // Dispatch custom event for toast display
        const event = new CustomEvent('showToast', {
            detail: { status, message }
        });
        document.dispatchEvent(event);
    }
}

// Initialize authentication when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new Authentication();
}); 