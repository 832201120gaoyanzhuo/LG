layui.use(['table', 'form', 'layer', 'upload'], function() {
    const table = layui.table;
    const form = layui.form;
    const layer = layui.layer;
    const upload = layui.upload;
    
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
            elem: '#contactTable',
            url: '/contacts/page',
            method: 'get',
            where: searchParams,
            height: 'full-85',
            page: true,
            limits: [10, 20, 30, 50],
            cols: [[
                {field: 'id', title: '主键', align: 'center', width: 80},
                {field: 'name', title: '姓名', align: 'center', width: 120,
                    templet: d => d.top == '1' ? 
                        `<div class="top-red"><i class="layui-icon layui-icon-star-fill"></i>${d.name}</div>` : d.name
                },
                {field: 'studentId', title: '学号', align: 'center', width: 120},
                {field: 'phone', title: '手机号码', align: 'center', width: 120},
                {field: 'address', title: '地址', align: 'center'},
                {field: 'email', title: '邮箱', align: 'center', width: 180},
                {field: 'sex', title: '性别', align: 'center', width: 80},
                {title: '操作', align: 'center', toolbar: '#tableToolbar', width: 280}
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
            // 重置表单
            $('#searchForm')[0].reset();
            // 清空搜索参数
            searchParams = {};
            // 重新加载表格
            reloadTable();
        });

        // 新增按钮点击
        $('#addContact').on('click', () => openDialog());

        // 表格工具栏事件
        table.on('tool(contactTable)', handleTableTool);

        // 导出按钮点击
        $('#exportBtn').on('click', handleExport);

        // 导入按钮点击
        $('#importBtn').on('click', handleImport);
    };

    // 处理表格工具栏事件
    const handleTableTool = (obj) => {
        const data = obj.data;
        const event = obj.event;
        
        switch(event) {
            case 'edit':
                openDialog(data.id);
                break;
            case 'delete':
                handleDelete(data.id);
                break;
            case 'top':
            case 'unTop':
                handleTop(data, event === 'top' ? '1' : '0');
                break;
            case 'favorite':
            case 'unfavorite':
                handleFavorite(data.id, event === 'favorite' ? '1' : '0');
                break;
        }
    };

    // 打开弹窗
    const openDialog = (id = '') => {
        layer.open({
            type: 2,
            title: id ? '编辑通讯录' : '添加通讯录',
            shadeClose: true,
            shade: 0.3,
            area: ['500px', '70%'],
            content: `/contacts/dialog${id ? '?id=' + id : ''}`
        });
    };

    // 处理删除
    const handleDelete = (id) => {
        layer.confirm('确定要删除吗？', {icon: 3}, function(index) {
            $.ajax({
                url: '/contacts/' + id,
                type: 'DELETE',
                success: res => {
                    if (res.status === 200) {
                        layer.msg(res.msg, {icon: 1});
                        reloadTable();
                    } else {
                        layer.msg(res.msg, {icon: 2});
                    }
                }
            });
            layer.close(index);
        });
    };

    // 处理置顶
    const handleTop = (data, topValue) => {
        const params = {...data, top: topValue};
        $.ajax({
            url: '/contacts',
            type: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify(params),
            success: res => {
                if (res.status === 200) {
                    layer.msg(res.msg, {icon: 1});
                    reloadTable();
                } else {
                    layer.msg(res.msg, {icon: 2});
                }
            }
        });
    };

    // 添加收藏处理函数
    const handleFavorite = (id, favorite) => {
        $.ajax({
            url: `/contacts/favorite/${id}?favorite=${favorite}`,
            type: 'PUT',
            success: res => {
                if (res.status === 200) {
                    layer.msg(favorite === '1' ? '收藏成功' : '取消收藏成功', {
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
        table.reload('contactTable', {
            where: searchParams
        });
    };

    // 处理导出
    const handleExport = () => {
        window.location.href = '/contacts/export';
    };

    // 处理导入
    const handleImport = () => {
        layer.open({
            type: 1,
            title: '导入通讯录',
            area: ['500px', '300px'],
            content: $('#importDialog').html(),
            success: function() {
                // 初始化上传组件
                upload.render({
                    elem: '#uploadFile',
                    url: '/contacts/import',
                    accept: 'file',
                    exts: 'xlsx',
                    done: function(res) {
                        if (res.status === 200) {
                            layer.msg('导入成功', {
                                icon: 1,
                                time: 2000
                            }, function() {
                                layer.closeAll();
                                reloadTable();
                            });
                        } else {
                            layer.msg(res.msg || '导入失败', {icon: 2});
                        }
                    },
                    error: function() {
                        layer.msg('导入失败', {icon: 2});
                    }
                });
            }
        });
    };

    // 初始化
    init();
}); 