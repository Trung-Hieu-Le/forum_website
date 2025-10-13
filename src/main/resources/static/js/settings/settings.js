// ============================================================================
// SETTINGS PAGE - INITIALIZATION
// ============================================================================

document.addEventListener('DOMContentLoaded', function() {
    initializeSettings();
});

function initializeSettings() {
    // Handle profile edit form submission
    const profileEditForm = document.getElementById('profileEditForm');
    if (profileEditForm) {
        profileEditForm.addEventListener('submit', handleProfileEdit);
    }

    // Handle password change form submission
    const changePasswordForm = document.getElementById('changePasswordForm');
    if (changePasswordForm) {
        changePasswordForm.addEventListener('submit', handlePasswordChange);
    }

    // Handle notification settings save
    const saveNotificationBtn = document.getElementById('saveNotificationSettings');
    if (saveNotificationBtn) {
        saveNotificationBtn.addEventListener('click', handleNotificationSave);
        // Load notification settings on page load
        loadNotificationSettings();
    }

    // Check browser notification support
    checkBrowserNotificationSupport();

    // Load current user data
    loadUserProfile();
}

// ============================================================================
// PROFILE MANAGEMENT
// ============================================================================

function handleProfileEdit(event) {
    event.preventDefault();
    
    // Clear previous validation errors
    clearSettingsValidationErrors(event.currentTarget);
    
    const formData = new FormData(event.target);
    const profileData = {
        username: formData.get('username'),
        email: formData.get('email'),
        phone: formData.get('phone'),
        fullname: formData.get('fullname')
    };

    // Show loading state
    const submitBtn = event.target.querySelector('button[type="submit"]');
    const originalText = submitBtn.textContent;
    submitBtn.textContent = 'Saving...';
    submitBtn.disabled = true;

    fetch('/api/settings/profile', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(profileData)
    })
    .then(response => response.json())
    .then(data => {
        if (data.status === 'ok') {
            showToast(data.type, data.message);
            // Close modal
            const modal = bootstrap.Modal.getInstance(document.getElementById('profileEditModal'));
            modal.hide();
            // Update UI with new data
            fetchAndUpdateUserData();
        } else {
            showToast(data.type, data.message);
            if (data.data) {
                displayFieldErrors(data.data);
            }
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showToast('error', 'An error occurred while updating profile');
    })
    .finally(() => {
        submitBtn.textContent = originalText;
        submitBtn.disabled = false;
    });
}

function loadUserProfile() {
    // Get user data from the page (passed from server via Thymeleaf)
    // The data is already available in the HTML via userAuth object
    // No need to fetch via API since it's server-side rendered
    console.log('User profile loaded from server-side rendering');
}

function updateProfileDisplay(userData) {
    // Update display fields in profile tab
    
    // Update email display
    const emailDisplays = document.querySelectorAll('.col-md-6 .form-control-plaintext');
    if (emailDisplays.length > 0) {
        emailDisplays.forEach(el => {
            if (el.textContent.includes('@') || el.textContent === 'N/A') {
                el.textContent = userData.email || 'N/A';
            }
        });
    }
    
    // Update fullname display
    const fullnameDisplay = document.querySelector('.col-md-6:nth-child(3) .form-control-plaintext');
    if (fullnameDisplay) {
        fullnameDisplay.textContent = userData.fullname || 'N/A';
    }
    
    // Update phone display
    const phoneDisplay = document.querySelector('.col-md-6:nth-child(4) .form-control-plaintext');
    if (phoneDisplay) {
        phoneDisplay.textContent = userData.phone || 'Not provided';
    }

    // Update avatar
    const avatarEl = document.getElementById('profileAvatar');
    if (avatarEl && userData.avatar) {
        const avatarUrl = userData.avatar.startsWith('/') ? userData.avatar : '/avatar/' + userData.avatar;
        avatarEl.src = avatarUrl;
    }

    // Update form fields in edit modal
    const editEmailEl = document.getElementById('editEmail');
    if (editEmailEl) {
        editEmailEl.value = userData.email || '';
    }
    
    const editPhoneEl = document.getElementById('editPhone');
    if (editPhoneEl) {
        editPhoneEl.value = userData.phone || '';
    }
    
    const editFullnameEl = document.getElementById('editFullname');
    if (editFullnameEl) {
        editFullnameEl.value = userData.fullname || '';
    }
    
    // Update header avatar if exists
    const headerAvatar = document.querySelector('.navbar img[alt="Avatar"]');
    if (headerAvatar && userData.avatar) {
        const avatarUrl = userData.avatar.startsWith('/') ? userData.avatar : '/avatar/' + userData.avatar;
        headerAvatar.src = avatarUrl;
    }
}

function fetchAndUpdateUserData() {
    fetch('/api/settings/current-user', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.status === 'ok' && data.data) {
            updateProfileDisplay(data.data);
        }
    })
    .catch(error => {
        console.error('Error fetching user data:', error);
    });
}

// ============================================================================
// AVATAR MANAGEMENT
// ============================================================================

function uploadAvatar(input) {
    if (input.files && input.files[0]) {
        const formData = new FormData();
        formData.append('avatar', input.files[0]);
        
        fetch('/api/settings/avatar', {
            method: 'POST',
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            if (data.status === 'ok') {
                showToast('success', data.message);
                // Reload page after 1 second to show all changes (locale will be preserved via cookie)
                setTimeout(() => {
                    window.location.reload();
                }, 1000);
            } else {
                showToast('error', data.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showToast('error', 'An error occurred while uploading avatar');
        });
    }
}

function updateAvatarDisplay(filename) {
    const avatarUrl = '/avatar/' + filename;
    
    // Update profile avatar
    const profileAvatar = document.getElementById('profileAvatar');
    if (profileAvatar) {
        profileAvatar.src = avatarUrl;
    }
    
    // Update header avatar (if exists)
    const headerAvatar = document.querySelector('.navbar img[alt="Avatar"]');
    if (headerAvatar) {
        headerAvatar.src = avatarUrl;
    }
    
    // Update all avatar images in the page
    document.querySelectorAll('img[alt="Avatar"]').forEach(img => {
        img.src = avatarUrl;
    });
}

// ============================================================================
// PASSWORD MANAGEMENT
// ============================================================================

function handlePasswordChange(event) {
    event.preventDefault();
    
    // Clear previous validation errors
    clearSettingsValidationErrors(event.currentTarget);
    
    const formData = new FormData(event.target);
    const passwordData = {
        currentPassword: formData.get('currentPassword'),
        newPassword: formData.get('newPassword'),
        confirmPassword: formData.get('confirmPassword')
    };

    // Show loading state
    const submitBtn = event.target.querySelector('button[type="submit"]');
    const originalText = submitBtn.textContent;
    submitBtn.textContent = 'Saving...';
    submitBtn.disabled = true;

    fetch('/api/settings/password', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(passwordData)
    })
    .then(response => response.json())
    .then(data => {
        if (data.status === 'ok') {
            showToast(data.type, data.message);
            // Close modal and clear form
            const modal = bootstrap.Modal.getInstance(document.getElementById('changePasswordModal'));
            modal.hide();
            event.target.reset();
        } else {
            showToast(data.type, data.message);
            if (data.data) {
                displayFieldErrors(data.data);
            }
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showToast('error', 'An error occurred while changing password');
    })
    .finally(() => {
        submitBtn.textContent = originalText;
        submitBtn.disabled = false;
    });
}

// ============================================================================
// NOTIFICATION SETTINGS
// ============================================================================

function handleNotificationSave() {
    const notificationSettings = {
        emailNewPost: document.getElementById('emailNewPost').checked,
        emailReply: document.getElementById('emailReply').checked,
        emailMention: document.getElementById('emailMention').checked,
        browserNotifications: document.getElementById('browserNotifications').checked
    };

    // Show loading state
    const saveBtn = document.getElementById('saveNotificationSettings');
    const originalText = saveBtn.textContent;
    saveBtn.textContent = 'Saving...';
    saveBtn.disabled = true;

    fetch('/api/settings/notifications', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(notificationSettings)
    })
    .then(response => response.json())
    .then(data => {
        if (data.status === 'ok') {
            showToast(data.type, data.message);
            // Settings are already updated in the UI
        } else {
            showToast(data.type, data.message);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showToast('error', 'An error occurred while saving notification settings');
    })
    .finally(() => {
        saveBtn.textContent = originalText;
        saveBtn.disabled = false;
    });
}

function loadNotificationSettings() {
    fetch('/api/settings/notifications', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.status === 'ok' && data.data) {
            // Update checkboxes with values from server
            document.getElementById('emailNewPost').checked = data.data.emailNewPost || false;
            document.getElementById('emailReply').checked = data.data.emailReply || false;
            document.getElementById('emailMention').checked = data.data.emailMention || false;
            document.getElementById('browserNotifications').checked = data.data.browserNotifications || false;
        }
    })
    .catch(error => {
        console.error('Error loading notification settings:', error);
    });
}

function checkBrowserNotificationSupport() {
    const browserNotificationsCheckbox = document.getElementById('browserNotifications');
    const browserNotificationAlert = document.getElementById('browserNotificationAlert');
    
    if (!browserNotificationsCheckbox || !browserNotificationAlert) {
        return;
    }
    
    if (!('Notification' in window)) {
        browserNotificationAlert.style.display = 'block';
        browserNotificationsCheckbox.disabled = true;
    } else if (Notification.permission === 'denied') {
        browserNotificationAlert.style.display = 'block';
        browserNotificationsCheckbox.disabled = true;
    }
}

// ============================================================================
// VALIDATION & ERROR HANDLING
// ============================================================================

function clearSettingsValidationErrors(form) {
    // Check if form is valid and has querySelectorAll method
    if (!form || typeof form.querySelectorAll !== 'function') {
        console.warn('clearSettingsValidationErrors: Invalid form element provided');
        return;
    }
    
    // Clear previous errors
    form.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));
    form.querySelectorAll('.invalid-feedback').forEach(el => el.remove());
    form.querySelectorAll('.is-invalid-msg').forEach(el => el.remove());
}

function displayFieldErrors(errors) {
    // Clear previous errors
    document.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));
    document.querySelectorAll('.invalid-feedback').forEach(el => el.remove());

    // Display new errors
    Object.keys(errors).forEach(fieldName => {
        const field = document.querySelector(`[name="${fieldName}"]`);
        if (field) {
            field.classList.add('is-invalid');
            const errorDiv = document.createElement('div');
            errorDiv.className = 'invalid-feedback is-invalid-msg';
            errorDiv.textContent = errors[fieldName];
            field.parentNode.appendChild(errorDiv);
        }
    });
}
