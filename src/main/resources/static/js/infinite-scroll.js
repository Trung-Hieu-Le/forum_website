$(document).ready(function() {
    let currentPage = typeof window.currentPage !== 'undefined' ? window.currentPage : 0;
    let totalPages = typeof window.totalPages !== 'undefined' ? window.totalPages : 1;
    let isLoading = false;

    // Function to format datetime
    function formatDateTime(dateString) {
        const date = new Date(dateString);
        const day = String(date.getDate()).padStart(2, '0');
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const year = date.getFullYear();
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        return `${day}/${month}/${year} ${hours}:${minutes}`;
    }
    
    // Function to escape HTML
    function escapeHtml(text) {
        if (!text) return '';
        const map = {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#039;'
        };
        return text.replace(/[&<>"']/g, m => map[m]);
    }
    
    // Function to render a post (reuse from home.js if available)
    function renderPost(thread) {
        if (typeof window.renderPost === 'function') {
            return window.renderPost(thread);
        }
        
        // Fallback rendering
        const user = thread.user || {};
        const topic = thread.topic || {};
        const avatar = user.avatar ? '/avatar/' + user.avatar : '/avatar/default-avatar.png';
        const username = user.username || 'Unknown';
        const createdAt = thread.createdAt ? formatDateTime(thread.createdAt) : '';
        const canEdit = window.userAuth && window.userAuth.id === user.id;
        
        let editButton = '';
        if (canEdit) {
            const editTitle = window.messages ? window.messages.postEdit : 'Edit';
            editButton = `
                <button class="btn btn-sm btn-outline-secondary edit-post-btn" 
                        data-thread-id="${thread.id}"
                        title="${editTitle}">
                    <i class="fas fa-edit"></i>
                </button>
            `;
        }
        
        return `
            <div class="post-card card mb-3 shadow-sm" data-thread-id="${thread.id}">
                <div class="card-body">
                    <div class="post-header d-flex align-items-center justify-content-between mb-3">
                        <div class="d-flex align-items-center">
                            <img src="${avatar}" 
                                 alt="Avatar" 
                                 class="post-avatar rounded-circle me-3"
                                 onerror="this.onerror=null;this.src='/avatar/default-avatar.png'">
                            <div>
                                <h6 class="mb-0 post-username">${escapeHtml(username)}</h6>
                                <small class="text-muted post-time">${createdAt}</small>
                            </div>
                        </div>
                        <div class="post-actions">
                            ${editButton}
                        </div>
                    </div>
                    <h5 class="post-title mb-2">${escapeHtml(thread.title || '')}</h5>
                    <div class="post-content mb-2">${thread.content || ''}</div>
                    <div class="post-footer mt-3 pt-2 border-top">
                        <span class="badge bg-secondary">${escapeHtml(topic.name || '')}</span>
                    </div>
                </div>
            </div>
        `;
    }

    $(window).scroll(function() {
        if ($(window).scrollTop() + $(window).height() >= $(document).height() - 100 && !isLoading && currentPage < totalPages - 1) {
            isLoading = true;
            $('#loading').show();
            currentPage++;

            $.ajax({
                url: '/api/threads?page=' + currentPage,
                type: 'GET',
                success: function(data) {
                    if (data.content && data.content.length > 0) {
                        data.content.forEach(function(thread) {
                            $('#thread-list').append(renderPost(thread));
                        });
                    }
                    totalPages = data.totalPages;
                    isLoading = false;
                    $('#loading').hide();
                },
                error: function() {
                    isLoading = false;
                    $('#loading').hide();
                }
            });
        }
    });
    
    // Function to reset infinite scroll (called after reloading posts)
    window.resetInfiniteScroll = function(data) {
        currentPage = 0;
        totalPages = data.totalPages || 1;
        window.currentPage = currentPage;
        window.totalPages = totalPages;
    };
});