// Settings page JavaScript functionality
document.addEventListener('DOMContentLoaded', function() {
    // Initialize settings functionality
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
    }

    // Check browser notification support
    checkBrowserNotificationSupport();

    // Load current user data
    loadUserProfile();
}

function handleProfileEdit(event) {
    event.preventDefault();
    
    // Clear previous validation errors
    clearValidationErrors(event.currentTarget);
    
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

    fetch('/settings/profile', {
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
            // Close modal and reload page
            const modal = bootstrap.Modal.getInstance(document.getElementById('profileEditModal'));
            modal.hide();
            setTimeout(() => {
                window.location.reload();
            }, 1500);
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

function handlePasswordChange(event) {
    event.preventDefault();
    
    // Clear previous validation errors
    clearValidationErrors(event.currentTarget);
    
    const formData = new FormData(event.target);
    const passwordData = {
        currentPassword: formData.get('currentPassword'),
        newPassword: formData.get('newPassword'),
        confirmPassword: formData.get('confirmPassword')
    };

    // Password confirmation validation will be handled by server

    // Show loading state
    const submitBtn = event.target.querySelector('button[type="submit"]');
    const originalText = submitBtn.textContent;
    submitBtn.textContent = 'Saving...';
    submitBtn.disabled = true;

    fetch('/settings/password', {
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

    fetch('/settings/notifications', {
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
            setTimeout(() => {
                window.location.reload();
            }, 1500);
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

function checkBrowserNotificationSupport() {
    if (!('Notification' in window)) {
        document.getElementById('browserNotificationAlert').style.display = 'block';
        document.getElementById('browserNotifications').disabled = true;
    } else if (Notification.permission === 'denied') {
        document.getElementById('browserNotificationAlert').style.display = 'block';
        document.getElementById('browserNotifications').disabled = true;
    }
}

function loadUserProfile() {
    // Get user data from the page (passed from server via Thymeleaf)
    // The data is already available in the HTML via userAuth object
    // No need to fetch via API since it's server-side rendered
    console.log('User profile loaded from server-side rendering');
}

function updateProfileDisplay(userData) {
    const usernameEl = document.getElementById('profileUsername');
    const emailEl = document.getElementById('profileEmail');
    const phoneEl = document.getElementById('profilePhone');
    const avatarEl = document.getElementById('profileAvatar');
    const editEmailEl = document.getElementById('editEmail');
    const editPhoneEl = document.getElementById('editPhone');
    const editFullnameEl = document.getElementById('editFullname');

    if (usernameEl && userData.username) {
        usernameEl.textContent = userData.username;
    }
    if (emailEl && userData.email) {
        emailEl.textContent = userData.email;
    }
    if (phoneEl) {
        phoneEl.textContent = userData.phone || 'Not provided';
    }
    if (avatarEl && userData.avatar) {
        avatarEl.src = userData.avatar;
    }

    // Update form fields
    if (editEmailEl) {
        editEmailEl.value = userData.email || '';
    }
    if (editPhoneEl) {
        editPhoneEl.value = userData.phone || '';
    }
    if (editFullnameEl) {
        editFullnameEl.value = userData.fullname || '';
    }
}

function clearValidationErrors(form) {
    // Check if form is valid and has querySelectorAll method
    if (!form || typeof form.querySelectorAll !== 'function') {
        console.warn('clearValidationErrors: Invalid form element provided');
        return;
    }
    
    // Clear previous errors
    form.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));
    form.querySelectorAll('.invalid-feedback').forEach(el => el.remove());
    form.querySelectorAll('.text-danger').forEach(el => el.remove());
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
            errorDiv.className = 'invalid-feedback';
            errorDiv.textContent = errors[fieldName];
            field.parentNode.appendChild(errorDiv);
        }
    });
}

// Avatar upload functionality
function uploadAvatar(input) {
    if (input.files && input.files[0]) {
        const formData = new FormData();
        formData.append('avatar', input.files[0]);
        
        fetch('/settings/avatar', {
            method: 'POST',
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            if (data.status === 'ok') {
                showToast('success', data.message);
                // Reload page to update all avatar references
                setTimeout(() => {
                    window.location.reload();
                }, 1500);
            } else {
                showToast('error', data.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showToast('error', error);
        });
    }
}