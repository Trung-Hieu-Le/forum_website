/**
 * Quill Editor Utility
 * Provides functions to initialize and manage Quill editors
 */

// Default Quill configuration
const QuillEditorConfig = {
    theme: 'snow',
    modules: {
        toolbar: [
            [{ 'header': [1, 2, 3, false] }],
            ['bold', 'italic', 'underline', 'strike'],
            ['link', 'image'],
            [{ 'list': 'ordered'}, { 'list': 'bullet' }],
            ['blockquote', 'code-block'],
            ['clean']
        ]
    }
};

/**
 * Initialize a Quill editor
 * @param {string} selector - CSS selector for the editor container
 * @param {string} placeholder - Placeholder text (optional)
 * @returns {Quill|null} - Quill instance or null if element not found
 */
function initQuillEditor(selector, placeholder) {
    const element = document.querySelector(selector);
    if (!element) {
        return null;
    }
    
    const config = {
        ...QuillEditorConfig,
        placeholder: placeholder || (window.messages ? window.messages.postEditorPlaceholder : 'Write something...')
    };
    
    return new Quill(selector, config);
}

/**
 * Get HTML content from Quill editor
 * @param {Quill} quill - Quill instance
 * @returns {string} - HTML content
 */
function getQuillContent(quill) {
    if (!quill) {
        return '';
    }
    return quill.root.innerHTML;
}

/**
 * Set HTML content to Quill editor
 * @param {Quill} quill - Quill instance
 * @param {string} content - HTML content to set
 */
function setQuillContent(quill, content) {
    if (!quill) {
        return;
    }
    
    if (content && content.trim()) {
        quill.root.innerHTML = content;
    } else {
        quill.setContents([]);
    }
}

/**
 * Clear Quill editor content
 * @param {Quill} quill - Quill instance
 */
function clearQuillContent(quill) {
    if (!quill) {
        return;
    }
    quill.setContents([]);
}

/**
 * Check if Quill editor is empty
 * @param {Quill} quill - Quill instance
 * @returns {boolean} - True if editor is empty
 */
function isQuillEmpty(quill) {
    if (!quill) {
        return true;
    }
    const text = quill.getText().trim();
    const html = quill.root.innerHTML.trim();
    return text === '' && (html === '' || html === '<p><br></p>');
}

