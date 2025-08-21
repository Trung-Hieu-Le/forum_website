/**
 * Validation Module
 * Handles client-side form validation
 */
class FormValidator {
    constructor() {
        this.init();
    }

    init() {
        this.bindEvents();
    }

    bindEvents() {
        // Add validation to all forms with validation class
        document.addEventListener('DOMContentLoaded', () => {
            this.initializeFormValidation();
        });
    }

    initializeFormValidation() {
        const forms = document.querySelectorAll('form[data-validate="true"]');
        forms.forEach(form => {
            this.setupFormValidation(form);
        });
    }

    setupFormValidation(form) {
        const inputs = form.querySelectorAll('input, select, textarea');
        
        inputs.forEach(input => {
            // Add validation on blur
            input.addEventListener('blur', () => this.validateField(input));
            
            // Add validation on input (for real-time feedback)
            input.addEventListener('input', () => this.clearFieldError(input));
        });

        // Add form submit validation
        form.addEventListener('submit', (e) => {
            if (!this.validateForm(form)) {
                e.preventDefault();
            }
        });
    }

    validateField(field) {
        const value = field.value.trim();
        const rules = this.getFieldRules(field);
        let isValid = true;
        let errorMessage = '';

        // Required validation
        if (rules.required && !value) {
            isValid = false;
            errorMessage = this.getErrorMessage('required', field);
        }

        // Email validation
        if (rules.email && value && !this.isValidEmail(value)) {
            isValid = false;
            errorMessage = this.getErrorMessage('email', field);
        }

        // Min length validation
        if (rules.minLength && value && value.length < rules.minLength) {
            isValid = false;
            errorMessage = this.getErrorMessage('minLength', field, rules.minLength);
        }

        // Max length validation
        if (rules.maxLength && value && value.length > rules.maxLength) {
            isValid = false;
            errorMessage = this.getErrorMessage('maxLength', field, rules.maxLength);
        }

        // Pattern validation
        if (rules.pattern && value && !rules.pattern.test(value)) {
            isValid = false;
            errorMessage = this.getErrorMessage('pattern', field);
        }

        // Custom validation
        if (rules.custom && typeof rules.custom === 'function') {
            const customResult = rules.custom(value, field);
            if (!customResult.isValid) {
                isValid = false;
                errorMessage = customResult.message || this.getErrorMessage('custom', field);
            }
        }

        if (!isValid) {
            this.showFieldError(field, errorMessage);
        } else {
            this.clearFieldError(field);
        }

        return isValid;
    }

    validateForm(form) {
        const inputs = form.querySelectorAll('input, select, textarea');
        let isFormValid = true;

        inputs.forEach(input => {
            if (!this.validateField(input)) {
                isFormValid = false;
            }
        });

        return isFormValid;
    }

    getFieldRules(field) {
        const rules = {};

        // Required
        if (field.hasAttribute('required')) {
            rules.required = true;
        }

        // Email
        if (field.type === 'email') {
            rules.email = true;
        }

        // Min length
        if (field.hasAttribute('minlength')) {
            rules.minLength = parseInt(field.getAttribute('minlength'));
        }

        // Max length
        if (field.hasAttribute('maxlength')) {
            rules.maxLength = parseInt(field.getAttribute('maxlength'));
        }

        // Pattern
        if (field.hasAttribute('pattern')) {
            rules.pattern = new RegExp(field.getAttribute('pattern'));
        }

        // Custom validation
        if (field.dataset.customValidation) {
            rules.custom = window[field.dataset.customValidation];
        }

        return rules;
    }

    isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    showFieldError(field, message) {
        this.clearFieldError(field);

        // Add error class
        field.classList.add('is-invalid');

        // Create error message element
        const errorDiv = document.createElement('div');
        errorDiv.className = 'invalid-feedback';
        errorDiv.textContent = message;

        // Insert error message after the field
        field.parentNode.appendChild(errorDiv);
    }

    clearFieldError(field) {
        field.classList.remove('is-invalid');
        
        // Remove existing error message
        const existingError = field.parentNode.querySelector('.invalid-feedback');
        if (existingError) {
            existingError.remove();
        }
    }

    getErrorMessage(type, field, param) {
        const fieldName = field.getAttribute('data-field-name') || field.name || 'This field';
        
        const messages = {
            required: `${fieldName} is required`,
            email: `${fieldName} must be a valid email address`,
            minLength: `${fieldName} must be at least ${param} characters long`,
            maxLength: `${fieldName} must not exceed ${param} characters`,
            pattern: `${fieldName} format is invalid`,
            custom: `${fieldName} is invalid`
        };

        return messages[type] || 'Invalid input';
    }

    // Static method to validate password strength
    static validatePasswordStrength(password) {
        const checks = {
            length: password.length >= 8,
            uppercase: /[A-Z]/.test(password),
            lowercase: /[a-z]/.test(password),
            numbers: /\d/.test(password),
            special: /[!@#$%^&*(),.?":{}|<>]/.test(password)
        };

        const score = Object.values(checks).filter(Boolean).length;
        
        if (score >= 4) return { strength: 'strong', score };
        if (score >= 3) return { strength: 'medium', score };
        return { strength: 'weak', score };
    }

    // Static method to validate password confirmation
    static validatePasswordConfirmation(password, confirmPassword) {
        return password === confirmPassword;
    }
}

// Initialize validation when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new FormValidator();
}); 