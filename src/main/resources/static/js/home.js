$(document).ready(function() {
    // Initialize Quill editor only if element exists (user is logged in)
    var quill = null;
    if ($('#post-editor').length > 0) {
        quill = initQuillEditor('#post-editor');
    }

    // Handle form submission (only if form exists)
    $('#create-post-form').on('submit', function(e) {
        e.preventDefault();
        
        const title = $('#post-title').val().trim();
        const content = getQuillContent(quill);
        const topicId = $('#post-topic').val();
        
        if (!title) {
            showToast('error', window.messages ? window.messages.postValidationTitleRequired : 'Please enter post title');
            return;
        }
        
        if (!topicId) {
            showToast('error', window.messages ? window.messages.postValidationTopicRequired : 'Please select a topic');
            return;
        }
        
        // Disable submit button
        const submitBtn = $('#submit-post-btn');
        const submitLoadingText = window.messages ? window.messages.postSubmitLoading : 'Posting...';
        submitBtn.prop('disabled', true);
        submitBtn.html('<span class="spinner-border spinner-border-sm me-2"></span>' + submitLoadingText);
        
        $.ajax({
            url: '/api/threads',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                title: title,
                content: content,
                topicId: parseInt(topicId)
            }),
            success: function(response) {
                if (response.status === 'ok') {
                    // Clear form
                    $('#post-title').val('');
                    clearQuillContent(quill);
                    $('#post-topic').val('');
                    
                    // Reload posts via AJAX
                    reloadPosts();
                    
                    const successMessage = window.messages ? window.messages.postSuccess : 'Post created successfully!';
                    showToast('success', response.message || successMessage);
                } else {
                    const errorMessage = window.messages ? window.messages.postError : 'An error occurred';
                    showToast('error', response.message || errorMessage);
                }
            },
            error: function(xhr) {
                let errorMessage = window.messages ? window.messages.postErrorCreate : 'An error occurred while creating post';
                if (xhr.responseJSON && xhr.responseJSON.message) {
                    errorMessage = xhr.responseJSON.message;
                }
                showToast('error', errorMessage);
            },
            complete: function() {
                const submitText = window.messages ? window.messages.postSubmit : 'Post';
                submitBtn.prop('disabled', false);
                submitBtn.html('<i class="fas fa-paper-plane me-2"></i>' + submitText);
            }
        });
    });
    
    // Function to reload posts via AJAX
    window.reloadPosts = function() {
        $.ajax({
            url: '/api/threads?page=0',
            type: 'GET',
            success: function(data) {
                const threadList = $('#thread-list');
                threadList.empty();
                
                if (data.content && data.content.length > 0) {
                    // Render posts (newest first, so they appear at the top)
                    data.content.forEach(function(thread) {
                        threadList.append(renderPost(thread));
                    });
                } else {
                    const emptyMessage = window.messages ? window.messages.postEmpty : 'No posts yet';
                    threadList.append('<div class="text-center py-4 text-muted">' + emptyMessage + '</div>');
                }
                
                // Reset infinite scroll state
                if (typeof resetInfiniteScroll === 'function') {
                    resetInfiniteScroll(data);
                }
                
                // Scroll to top to show the new post
                $('html, body').animate({
                    scrollTop: 0
                }, 300);
            },
            error: function() {
                const errorMessage = window.messages ? window.messages.postErrorLoad : 'Unable to load post list';
                showToast('error', errorMessage);
            }
        });
    };
    
    // Function to render a post
    function renderPost(thread) {
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
    
    // Helper function to format datetime
    function formatDateTime(dateString) {
        const date = new Date(dateString);
        const day = String(date.getDate()).padStart(2, '0');
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const year = date.getFullYear();
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        return `${day}/${month}/${year} ${hours}:${minutes}`;
    }
    
    // Helper function to escape HTML
    function escapeHtml(text) {
        const map = {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#039;'
        };
        return text ? text.replace(/[&<>"']/g, m => map[m]) : '';
    }
    
    // Make renderPost available globally for infinite scroll
    window.renderPost = renderPost;
    
    // Initialize Quill editor for edit modal
    let editQuill = null;
    const editModal = new bootstrap.Modal(document.getElementById('editPostModal'));
    
    // Handle edit button click
    $(document).on('click', '.edit-post-btn', function() {
        const threadId = $(this).data('thread-id');
        loadThreadForEdit(threadId);
    });
    
    // Function to load thread data for editing
    function loadThreadForEdit(threadId) {
        $.ajax({
            url: '/api/threads/' + threadId,
            type: 'GET',
            success: function(response) {
                if (response.status === 'ok' && response.data && response.data.thread) {
                    const thread = response.data.thread;
                    
                    // Set form values
                    $('#edit-thread-id').val(thread.id);
                    $('#edit-post-title').val(thread.title);
                    $('#edit-post-topic').val(thread.topic.id);
                    
                    // Initialize Quill editor if not already initialized
                    if (!editQuill) {
                        editQuill = initQuillEditor('#edit-post-editor');
                    }
                    
                    // Set Quill content
                    setQuillContent(editQuill, thread.content);
                    
                    // Show modal
                    editModal.show();
                } else {
                    const errorMessage = window.messages ? window.messages.postErrorLoad : 'Unable to load post';
                    showToast('error', errorMessage);
                }
            },
            error: function() {
                const errorMessage = window.messages ? window.messages.postErrorLoad : 'Unable to load post';
                showToast('error', errorMessage);
            }
        });
    }
    
    // Handle update button click
    $('#update-post-btn').on('click', function() {
        const threadId = $('#edit-thread-id').val();
        const title = $('#edit-post-title').val().trim();
        const content = getQuillContent(editQuill);
        const topicId = $('#edit-post-topic').val();
        
        if (!title) {
            showToast('error', window.messages ? window.messages.postValidationTitleRequired : 'Please enter post title');
            return;
        }
        
        if (!topicId) {
            showToast('error', window.messages ? window.messages.postValidationTopicRequired : 'Please select a topic');
            return;
        }
        
        // Disable update button
        const updateBtn = $('#update-post-btn');
        const updateLoadingText = window.messages ? window.messages.postUpdateSubmitLoading : 'Updating...';
        updateBtn.prop('disabled', true);
        updateBtn.html('<span class="spinner-border spinner-border-sm me-2"></span>' + updateLoadingText);
        
        $.ajax({
            url: '/api/threads/' + threadId,
            type: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify({
                title: title,
                content: content,
                topicId: parseInt(topicId)
            }),
            success: function(response) {
                if (response.status === 'ok') {
                    // Close modal
                    editModal.hide();
                    
                    // Reload posts
                    reloadPosts();
                    
                    const successMessage = window.messages ? window.messages.postUpdateSuccess : 'Post updated successfully!';
                    showToast('success', response.message || successMessage);
                } else {
                    const errorMessage = window.messages ? window.messages.postError : 'An error occurred';
                    showToast('error', response.message || errorMessage);
                }
            },
            error: function(xhr) {
                let errorMessage = window.messages ? window.messages.postUpdateError : 'An error occurred while updating post';
                if (xhr.responseJSON && xhr.responseJSON.message) {
                    errorMessage = xhr.responseJSON.message;
                }
                showToast('error', errorMessage);
            },
            complete: function() {
                const updateText = window.messages ? window.messages.postUpdateSubmit : 'Update';
                updateBtn.prop('disabled', false);
                updateBtn.html('<i class="fas fa-save me-2"></i>' + updateText);
            }
        });
    });
    
    // Reset form when modal is hidden
    $('#editPostModal').on('hidden.bs.modal', function() {
        $('#edit-post-form')[0].reset();
        clearQuillContent(editQuill);
    });
});


