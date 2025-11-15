function showToast(status, message) {
    const toastStyles = {
        info: { color: '#0d6efd' },
        success: { color: '#198754' },
        warning: { color: '#ffc107' },
        error: { color: '#dc3545' }
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

    // Get template from HTML
    const template = document.getElementById('toast-template');
    let $toast;
    
    if (template) {
        // Clone template
        const toastElement = template.content.cloneNode(true);
        $toast = $(toastElement.querySelector('.toast'));
    } else {
        // Fallback: create toast HTML if template not found
        const toastHtml = `
            <div class="toast" role="alert" aria-live="assertive" aria-atomic="true" style="position: relative;">
                <div class="toast-body d-flex align-items-center" style="padding: 12px 16px;">
                    <i class="fas me-2"></i>
                    <span class="me-auto"></span>
                    <button type="button" class="btn-close ms-2" data-bs-dismiss="toast" aria-label="Close"></button>
                    <div class="progress w-100 position-absolute bottom-0 start-0" style="height: 4px;">
                        <div class="progress-bar" style="width: 100%;" role="progressbar"></div>
                    </div>
                </div>
            </div>
        `;
        $toast = $(toastHtml);
    }
    
    // Set attributes and classes
    $toast.attr('id', toastId);
    $toast.addClass(`toast-${finalStatus}`);
    $toast.attr('data-bs-autohide', 'true');
    $toast.attr('data-bs-delay', toastDelay);
    
    // Set content
    const $toastBody = $toast.find('.toast-body');
    $toastBody.css('color', toastStyles[finalStatus].color);
    
    const $icon = $toast.find('i');
    $icon.addClass(icons[finalStatus]);
    $icon.css('color', toastStyles[finalStatus].color);
    
    $toast.find('.me-auto').text(message);
    
    const $progressBar = $toast.find('.progress-bar');
    $progressBar.css('background-color', toastStyles[finalStatus].color);

    // Append to container
    $('.toast-container').append($toast);
    
    // Initialize and show toast
    const toast = new bootstrap.Toast($toast[0]);
    toast.show();

    // Animate progress bar
    setTimeout(() => {
        $progressBar
            .css('width', '0%')
            .css('transition', `width ${toastDelay}ms linear`);
    }, 10);

    $toast.on('hidden.bs.toast', () => $toast.remove());
}