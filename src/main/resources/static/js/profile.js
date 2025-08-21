// Profile Page JavaScript
$(document).ready(function() {
    initializeProfile();
    loadGroups();
    loadFriends();
});

function initializeProfile() {
    // Initialize privacy toggle
    const privacyToggle = $('#privacyToggle');
    privacyToggle.on('click', function() {
        togglePrivacy();
    });

    // Initialize form validations
    $('#editProfileForm').on('submit', function(e) {
        e.preventDefault();
        updateProfile();
    });

    $('#changePasswordForm').on('submit', function(e) {
        e.preventDefault();
        changePassword();
    });

    // Initialize avatar preview
    $('#avatar').on('input', function() {
        previewAvatar(this.value);
    });

    // Initialize tooltips
    $('[data-bs-toggle="tooltip"]').tooltip();
}

function updateProfile() {
    const formData = {
        fullname: $('#fullname').val(),
        avatar: $('#avatar').val(),
        phone: $('#phone').val(),
        email: $('#email').val()
    };

    if (!validateProfileForm(formData)) {
        return;
    }

    showLoading('#editProfileModal .modal-body');

    $.ajax({
        url: '/api/profile/update',
        type: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(formData),
        success: function(response) {
            hideLoading('#editProfileModal .modal-body');
            showToast('success', 'Cập nhật profile thành công!');
            $('#editProfileModal').modal('hide');
            updateProfileDisplay(response.data);
        },
        error: function(xhr) {
            hideLoading('#editProfileModal .modal-body');
            let errorMessage = 'Có lỗi xảy ra khi cập nhật profile';
            if (xhr.responseJSON && xhr.responseJSON.message) {
                errorMessage = xhr.responseJSON.message;
            }
            showToast('error', errorMessage);
        }
    });
}

function changePassword() {
    const formData = {
        currentPassword: $('#currentPassword').val(),
        newPassword: $('#newPassword').val(),
        confirmPassword: $('#confirmPassword').val()
    };

    if (!validatePasswordForm(formData)) {
        return;
    }

    showLoading('#changePasswordModal .modal-body');

    $.ajax({
        url: '/api/profile/change-password',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(formData),
        success: function(response) {
            hideLoading('#changePasswordModal .modal-body');
            showToast('success', 'Đổi mật khẩu thành công!');
            $('#changePasswordModal').modal('hide');
            $('#changePasswordForm')[0].reset();
        },
        error: function(xhr) {
            hideLoading('#changePasswordModal .modal-body');
            let errorMessage = 'Có lỗi xảy ra khi đổi mật khẩu';
            if (xhr.responseJSON && xhr.responseJSON.message) {
                errorMessage = xhr.responseJSON.message;
            }
            showToast('error', errorMessage);
        }
    });
}

function togglePrivacy() {
    const toggle = $('#privacyToggle');
    const isPrivate = toggle.hasClass('active');

    showLoading('.privacy-toggle');

    $.ajax({
        url: '/api/profile/privacy',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({ privateProfile: !isPrivate }),
        success: function(response) {
            hideLoading('.privacy-toggle');
            if (response.data.privateProfile) {
                toggle.addClass('active');
                showToast('info', 'Trang cá nhân đã được khóa');
            } else {
                toggle.removeClass('active');
                showToast('info', 'Trang cá nhân đã được mở');
            }
        },
        error: function(xhr) {
            hideLoading('.privacy-toggle');
            let errorMessage = 'Có lỗi xảy ra khi thay đổi cài đặt riêng tư';
            if (xhr.responseJSON && xhr.responseJSON.message) {
                errorMessage = xhr.responseJSON.message;
            }
            showToast('error', errorMessage);
        }
    });
}

function loadGroups() {
    showLoading('#groupsList');

    $.ajax({
        url: '/api/profile/groups',
        type: 'GET',
        success: function(response) {
            hideLoading('#groupsList');
            displayGroups(response.data);
        },
        error: function(xhr) {
            hideLoading('#groupsList');
            $('#groupsList').html(`
                <div class="empty-state">
                    <i class="fas fa-exclamation-triangle"></i>
                    <p>Không thể tải danh sách nhóm</p>
                </div>
            `);
        }
    });
}

function loadFriends() {
    showLoading('#friendsList');

    $.ajax({
        url: '/api/profile/friends',
        type: 'GET',
        success: function(response) {
            hideLoading('#friendsList');
            displayFriends(response.data);
        },
        error: function(xhr) {
            hideLoading('#friendsList');
            $('#friendsList').html(`
                <div class="empty-state">
                    <i class="fas fa-exclamation-triangle"></i>
                    <p>Không thể tải danh sách bạn bè</p>
                </div>
            `);
        }
    });
}

function displayGroups(groups) {
    const container = $('#groupsList');
    
    if (!groups || groups.length === 0) {
        container.html(`
            <div class="empty-state">
                <i class="fas fa-users"></i>
                <p>Chưa tham gia nhóm nào</p>
            </div>
        `);
        return;
    }

    let html = '';
    groups.forEach(group => {
        html += `
            <div class="list-item" onclick="viewGroup(${group.id})">
                <img src="${group.avatar || '/images/default-group.png'}" 
                     alt="${group.name}" class="list-item-avatar">
                <div class="list-item-info">
                    <div class="list-item-name">${group.name}</div>
                    <div class="list-item-meta">${group.memberCount} thành viên</div>
                </div>
            </div>
        `;
    });

    container.html(html);
}

function displayFriends(friends) {
    const container = $('#friendsList');
    
    if (!friends || friends.length === 0) {
        container.html(`
            <div class="empty-state">
                <i class="fas fa-user-friends"></i>
                <p>Chưa có bạn bè nào</p>
            </div>
        `);
        return;
    }

    let html = '';
    friends.forEach(friend => {
        const statusClass = friend.isOnline ? 'text-success' : 'text-muted';
        const statusText = friend.isOnline ? 'Đang online' : 'Offline';
        
        html += `
            <div class="list-item" onclick="viewProfile('${friend.username}')">
                <img src="${friend.avatar || '/images/default-avatar.png'}" 
                     alt="${friend.fullname || friend.username}" class="list-item-avatar">
                <div class="list-item-info">
                    <div class="list-item-name">${friend.fullname || friend.username}</div>
                    <div class="list-item-meta ${statusClass}">${statusText}</div>
                </div>
            </div>
        `;
    });

    container.html(html);
}

function validateProfileForm(data) {
    // Reset previous errors
    $('.form-control').removeClass('is-invalid');
    $('.invalid-feedback').remove();

    let isValid = true;

    // Validate fullname
    if (!data.fullname || data.fullname.trim().length < 2) {
        showFieldError('#fullname', 'Họ tên phải có ít nhất 2 ký tự');
        isValid = false;
    }

    // Validate email
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!data.email || !emailRegex.test(data.email)) {
        showFieldError('#email', 'Email không hợp lệ');
        isValid = false;
    }

    // Validate phone (optional)
    if (data.phone && data.phone.trim() !== '') {
        const phoneRegex = /^[0-9+\-\s()]{10,15}$/;
        if (!phoneRegex.test(data.phone)) {
            showFieldError('#phone', 'Số điện thoại không hợp lệ');
            isValid = false;
        }
    }

    return isValid;
}

function validatePasswordForm(data) {
    // Reset previous errors
    $('.form-control').removeClass('is-invalid');
    $('.invalid-feedback').remove();

    let isValid = true;

    // Validate current password
    if (!data.currentPassword) {
        showFieldError('#currentPassword', 'Vui lòng nhập mật khẩu hiện tại');
        isValid = false;
    }

    // Validate new password
    if (!data.newPassword || data.newPassword.length < 6) {
        showFieldError('#newPassword', 'Mật khẩu mới phải có ít nhất 6 ký tự');
        isValid = false;
    }

    // Validate confirm password
    if (data.newPassword !== data.confirmPassword) {
        showFieldError('#confirmPassword', 'Xác nhận mật khẩu không khớp');
        isValid = false;
    }

    return isValid;
}

function showFieldError(fieldSelector, message) {
    const field = $(fieldSelector);
    field.addClass('is-invalid');
    field.after(`<div class="invalid-feedback">${message}</div>`);
}

function previewAvatar(url) {
    if (url && url.trim() !== '') {
        $('.profile-avatar').attr('src', url);
    } else {
        $('.profile-avatar').attr('src', '/images/default-avatar.png');
    }
}

function updateProfileDisplay(userData) {
    // Update profile display with new data
    $('.profile-username').text(userData.fullname || userData.username);
    $('.profile-handle').text('@' + userData.username);
    $('.profile-avatar').attr('src', userData.avatar || '/images/default-avatar.png');
    
    // Update sidebar info
    $('#sidebarEmail').text(userData.email);
    $('#sidebarPhone').text(userData.phone || 'Chưa cập nhật');
}

function showLoading(selector) {
    const container = $(selector);
    const originalContent = container.html();
    container.data('original-content', originalContent);
    container.html(`
        <div class="loading-spinner">
            <div class="spinner"></div>
        </div>
    `);
}

function hideLoading(selector) {
    const container = $(selector);
    const originalContent = container.data('original-content');
    if (originalContent) {
        container.html(originalContent);
    }
}

function showToast(type, message) {
    // Create toast element
    const toastId = 'toast-' + Date.now();
    const iconClass = type === 'success' ? 'fa-check-circle' : 
                     type === 'error' ? 'fa-exclamation-circle' : 'fa-info-circle';
    const bgClass = type === 'success' ? 'bg-success' : 
                   type === 'error' ? 'bg-danger' : 'bg-info';

    const toastHtml = `
        <div id="${toastId}" class="toast align-items-center text-white ${bgClass} border-0" role="alert">
            <div class="d-flex">
                <div class="toast-body">
                    <i class="fas ${iconClass} me-2"></i>
                    ${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        </div>
    `;

    // Add to toast container
    let toastContainer = $('.toast-container');
    if (toastContainer.length === 0) {
        $('body').append('<div class="toast-container position-fixed top-0 end-0 p-3"></div>');
        toastContainer = $('.toast-container');
    }

    toastContainer.append(toastHtml);

    // Show toast
    const toast = new bootstrap.Toast(document.getElementById(toastId));
    toast.show();

    // Remove toast after it's hidden
    $(`#${toastId}`).on('hidden.bs.toast', function() {
        $(this).remove();
    });
}

function viewGroup(groupId) {
    window.location.href = `/groups/${groupId}`;
}

function viewProfile(username) {
    window.location.href = `/profile/${username}`;
}

// Tab switching functionality
function switchTab(tabName) {
    // Update tab active state
    $('.nav-link').removeClass('active');
    $(`[onclick="switchTab('${tabName}')"]`).addClass('active');

    // Show/hide content
    $('.tab-content').hide();
    $(`#${tabName}Content`).show();
}

// Modal event handlers
$('#editProfileModal').on('show.bs.modal', function() {
    // Reset form validation
    $('.form-control').removeClass('is-invalid');
    $('.invalid-feedback').remove();
});

$('#changePasswordModal').on('show.bs.modal', function() {
    // Reset form
    $('#changePasswordForm')[0].reset();
    $('.form-control').removeClass('is-invalid');
    $('.invalid-feedback').remove();
});

$('#changePasswordModal').on('hidden.bs.modal', function() {
    // Clear form
    $('#changePasswordForm')[0].reset();
});

// Initialize tabs on page load
$(document).ready(function() {
    // Show first tab by default
    switchTab('groups');
}); 