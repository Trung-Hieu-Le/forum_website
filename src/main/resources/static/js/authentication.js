$(document).ready(function () {
    const status = /*[[${toastStatus}]]*/ 'null';
    const message = /*[[${toastMessage}]]*/ 'null';
    if (status !== 'null' && message !== 'null') {
        showToast(status, message);
    }

    $('form.auth-form').submit(function (e) {
        e.preventDefault();
        const $form = $(this);
        const url = $form.attr('action');
        const method = $form.attr('method') || 'POST';
        const formData = {};
        $form.serializeArray().forEach(({ name, value }) => formData[name] = value);

        $.ajax({
            url: url,
            type: method,
            contentType: 'application/json',
            data: JSON.stringify(formData)
        }).done(function (resp) {
            if (resp.messages && Array.isArray(resp.messages)) {
                resp.messages.forEach(msg => showToast(resp.errorTag, msg));
            } else {
                showToast(resp.errorTag, resp.message);
            }

            if (resp.status === 'ok' && resp.redirectUrl) {
                setTimeout(() => window.location.href = resp.redirectUrl, 800);
            }
        }).fail(function () {
            showToast('danger', 'Unknown error');
        });
    });
});