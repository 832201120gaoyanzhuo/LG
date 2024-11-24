layui.use(['layer'], function() {
    const layer = layui.layer;
    
    // 页面初始化
    const init = () => {
        bindEvents();
        loadDefaultPage();
    };

    // 绑定事件
    const bindEvents = () => {
        // 菜单点击事件
        $('.menu-item').on('click', function() {
            const url = $(this).data('url');
            loadPage(url);
            $(this).addClass('active').siblings().removeClass('active');
        });

        // iframe加载事件
        $('#mainFrame').on('load', () => layer.closeAll('loading'));
    };

    // 加载页面
    const loadPage = (url) => {
        layer.load(2);
        $('#mainFrame').attr('src', url);
    };

    // 加载默认页面
    const loadDefaultPage = () => {
        const defaultUrl = '/contacts/contactsList';
        loadPage(defaultUrl);
    };

    // 初始化
    init();
}); 