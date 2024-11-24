layui.use(['table', 'form', 'layer'], function() {
    const table = layui.table;
    const form = layui.form;
    const layer = layui.layer;
    
    // 查询参数
    let searchParams = {};

    // 页面初始化
    const init = () => {
        initTable();
        bindEvents();
    };

    // 初始化表格
    const initTable = () => {
        table.render({
            elem: '#favoriteTable',
            url: '/contacts/favorites/page',
            method: 'get',
            where: searchParams,
            height: 'full-85',
            page: true,
            limits: [10, 20, 30, 50],
            cols: [[
                {field: 'id', title: '主键', align: 'center', width: 80},
                {field: 'name', title: '姓名', align: 'center', width: 120},
                {field: 'studentId', title: '学号', align: 'center', width: 120},
                {field: 'phone', title: '手机号码', align: 'center', width: 120},
                {field: 'address', title: '地址', align: 'center'},
                {field: 'email', title: '邮箱', align: 'center', width: 180},
                {field: 'sex', title: '性别', align: 'center', width: 80},
                {title: '操作', align: 'center', toolbar: '#tableToolbar', width: 120}
            ]],
            response: {
                statusCode: 200
            },
            parseData: res => ({
                code: res.status,
                msg: res.msg,
                count: res.data.total,
                data: res.data.records
            })
        });
    };

    // 绑定事件
    const bindEvents = () => {
        // 搜索表单提交
        form.on('submit(searchSubmit)', data => {
            searchParams = data.field;
            reloadTable();
            return false;
        });

        // 重置按钮点击
        $('#resetBtn').on('click', () => {
            $('#searchForm')[0].reset();
            searchParams = {};
            reloadTable();
        });

        // 表格工具栏事件
        table.on('tool(favoriteTable)', handleTableTool);
    };

    // 处理表格工具栏事件
    const handleTableTool = (obj) => {
        if (obj.event === 'unfavorite') {
            handleFavorite(obj.data.id, '0');
        }
    };

    // 处理取消收藏
    const handleFavorite = (id, favorite) => {
        $.ajax({
            url: `/contacts/favorite/${id}?favorite=${favorite}`,
            type: 'PUT',
            success: res => {
                if (res.status === 200) {
                    layer.msg('取消收藏成功', {
                        icon: 1,
                        time: 2000
                    });
                    reloadTable();
                } else {
                    layer.msg(res.msg, {icon: 2});
                }
            }
        });
    };

    // 重新加载表格
    const reloadTable = () => {
        table.reload('favoriteTable', {
            where: searchParams
        });
    };

    // 初始化
    init();
}); 