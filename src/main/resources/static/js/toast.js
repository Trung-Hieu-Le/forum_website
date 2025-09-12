function showToast(status, message) {
    const toastStyles = {
        info: { headerBg: '#cfe2ff', color: '#0d6efd' },
        success: { headerBg: '#d1e7dd', color: '#198754' },
        warning: { headerBg: '#fff3cd', color: '#ffc107' },
        error: { headerBg: '#f8d7da', color: '#dc3545' }
    };
    const icons = {
        info: 'fa-info-circle',
        success: 'fa-check-circle',
        warning: 'fa-exclamation-triangle',
        error: 'fa-times-circle'
    };

    // Normalize status to lowercase and fallback to error
    const normalizedStatus = status ? status.toLowerCase() : 'error';
    const finalStatus = toastStyles[normalizedStatus] ? normalizedStatus : 'error';

    const isError = finalStatus === 'error';
    const toastDelay = isError ? 7000 : 3000; // Error: 7s, Others: 3s
    const toastId = `toast-${Date.now()}`;

    const toastHtml = `
        <div id="${toastId}" class="toast toast-${finalStatus}" role="alert" aria-live="assertive" aria-atomic="true"
            style="background-color: ${toastStyles[finalStatus].headerBg};"
            data-bs-autohide="true" data-bs-delay="${toastDelay}">
            <div class="toast-header" style="background-color: ${toastStyles[finalStatus].headerBg}; color: ${toastStyles[finalStatus].color}">
                <i class="fas ${icons[finalStatus]} me-2" style="color: ${toastStyles[finalStatus].color}"></i>
                <strong class="me-auto">${finalStatus.charAt(0).toUpperCase() + finalStatus.slice(1)}</strong>
                <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
                <div class="progress w-100 mt-2">
                    <div class="progress-bar" style="width: 100%; background-color: ${toastStyles[finalStatus].color}; height: 4px;" role="progressbar"></div>
                </div>
            </div>
            <div class="toast-body">${message}</div>
        </div>
    `;

    $('.toast-container').append(toastHtml);
    const $toast = $(`#${toastId}`);
    const toast = new bootstrap.Toast($toast[0]);
    toast.show();

    setTimeout(() => {
        $toast.find('.progress-bar')
            .css('width', '0%')
            .css('transition', `width ${toastDelay}ms linear`);
    }, 10);

    $toast.on('hidden.bs.toast', () => $toast.remove());
}