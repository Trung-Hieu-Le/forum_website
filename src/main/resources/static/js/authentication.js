// Handle authentication forms (login, register, forgot password, reset password)
$(document).ready(function () {
    const status = /*[[${toastStatus}]]*/ 'null';
    const message = /*[[${toastMessage}]]*/ 'null';
    if (status !== 'null' && message !== 'null') {
        showToast(status, message);
    }

    $('form[action="/login"]').submit(function (e) {
        e.preventDefault();
        handleLogin($(this));
    });

    $('form[action="/register"]').submit(function (e) {
        e.preventDefault();
        handleRegister($(this));
    });

    $('form[action="/forgot-password"]').submit(function (e) {
        e.preventDefault();
        handleForgotPassword($(this));
    });

    $('form[action="/reset-password"]').submit(function (e) {
        e.preventDefault();
        handleResetPassword($(this));
    });
});

function clearValidationErrors($form) {
    $form.find('.is-invalid').each(function() {
        $(this).removeClass('is-invalid');
        $(this).next('.text-danger').remove();
    });
}

function handleValidationErrors($form, resp) {
    if (resp.data && typeof resp.data === 'object') {
        clearValidationErrors($form);
        
        Object.keys(resp.data).forEach(fieldName => {
            const $field = $form.find(`[name="${fieldName}"]`);
            if ($field.length) {
                $field.addClass('is-invalid');
                $field.after(`<div class="text-danger">${resp.data[fieldName]}</div>`);
            }
        });
        return true;
    }
    return false;
}

function handleLogin($form) {
    const formData = {};
    $form.serializeArray().forEach(({ name, value }) => formData[name] = value);

    $.ajax({
        url: '/login',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(formData)
    }).done(function (resp) {
        if (resp.status === 'ok') {
            clearValidationErrors($form);
            showToast(resp.type.toLowerCase(), resp.message);
            setTimeout(() => window.location.href = '/', 800);
        } else {
            if (handleValidationErrors($form, resp)) {
                showToast(resp.type.toLowerCase(), resp.message);
            } else {
                clearValidationErrors($form);
                showToast(resp.type.toLowerCase(), resp.message);
            }
        }
    }).fail(function () {
        clearValidationErrors($form);
        showToast('error', /*[[#{error.unknown}]]*/ 'Unknown error');
    });
}

function handleRegister($form) {
    const formData = {};
    $form.serializeArray().forEach(({ name, value }) => formData[name] = value);

    $.ajax({
        url: '/register',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(formData)
    }).done(function (resp) {
        if (resp.status === 'ok') {
            clearValidationErrors($form);
            showToast(resp.type.toLowerCase(), resp.message);
            setTimeout(() => window.location.href = '/login', 800);
        } else {
            if (handleValidationErrors($form, resp)) {
                showToast(resp.type.toLowerCase(), resp.message);
            } else {
                clearValidationErrors($form);
                showToast(resp.type.toLowerCase(), resp.message);
            }
        }
    }).fail(function () {
        clearValidationErrors($form);
        showToast('error', /*[[#{error.unknown}]]*/ 'Unknown error');
    });
}

function handleForgotPassword($form) {
    const formData = {};
    $form.serializeArray().forEach(({ name, value }) => formData[name] = value);

    $.ajax({
        url: '/forgot-password',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(formData)
    }).done(function (resp) {
        showToast(resp.type.toLowerCase(), resp.message);
        
        if (resp.status === 'ok') {
            setTimeout(() => window.location.href = '/reset-password', 800);
        }
    }).fail(function () {
        showToast('error', /*[[#{error.unknown}]]*/ 'Unknown error');
    });
}

function handleResetPassword($form) {
    const formData = {};
    $form.serializeArray().forEach(({ name, value }) => formData[name] = value);

    $.ajax({
        url: '/reset-password',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(formData)
    }).done(function (resp) {
        if (resp.status === 'ok') {
            clearValidationErrors($form);
            showToast(resp.type.toLowerCase(), resp.message);
            setTimeout(() => window.location.href = '/login', 800);
        } else {
            if (handleValidationErrors($form, resp)) {
                showToast(resp.type.toLowerCase(), resp.message);
            } else {
                clearValidationErrors($form);
                showToast(resp.type.toLowerCase(), resp.message);
            }
        }
    }).fail(function () {
        clearValidationErrors($form);
        showToast('error', /*[[#{error.unknown}]]*/ 'Unknown error');
    });
}