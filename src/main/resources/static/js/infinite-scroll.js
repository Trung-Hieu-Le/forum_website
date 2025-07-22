$(document).ready(function() {
    let currentPage = [[${currentPage}]];
    const totalPages = [[${totalPages}]];
    let isLoading = false;

    $(window).scroll(function() {
        if ($(window).scrollTop() + $(window).height() >= $(document).height() - 100 && !isLoading && currentPage < totalPages - 1) {
            isLoading = true;
            $('#loading').show();
            currentPage++;

            $.ajax({
                url: '/api/threads?page=' + currentPage,
                type: 'GET',
                success: function(data) {
                    data.content.forEach(function(thread) {
                        $('#thread-list').append(
                            '<div class="card mb-3">' +
                                '<div class="card-body">' +
                                    '<h5 class="card-title">' + thread.title + '</h5>' +
                                    '<p class="card-text">' + thread.content + '</p>' +
                                    '<p class="card-text"><small class="text-muted">Topic: ' + thread.topic.name + ' | Posted on ' + new Date(thread.createdAt).toLocaleString() + '</small></p>' +
                                '</div>' +
                            '</div>'
                        );
                    });
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
});