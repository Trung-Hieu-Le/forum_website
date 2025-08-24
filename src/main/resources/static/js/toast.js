function showToast(status, message) {
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

    $('.toast-container').append(toastHtml);
    const $toast = $(`#${toastId}`);
    const toast = new bootstrap.Toast($toast[0]);
    toast.show();

    if (!isDanger) {
        setTimeout(() => {
            $toast.find('.progress-bar')
                .css('width', '0%')
                .css('transition', `width ${toastDelay}ms linear`);
        }, 10);
    }

    $toast.on('hidden.bs.toast', () => $toast.remove());
}