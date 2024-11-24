layui.use(['form', 'layer'], function() {
    const form = layui.form;
    const layer = layui.layer;
    
    // 页面初始化
    const init = () => {
        bindEvents();
        initFormData();
    };

    // 绑定事件
    const bindEvents = () => {
        // 表单提交
        form.on('submit(submitForm)', data => {
            handleSubmit(data.field);
            return false;
        });
    };

    // 初始化表单数据
    const initFormData = () => {
        const id = getUrlParam('id');
        if (id) {
            // 编辑模式，加载数据
            loadContactData(id);
        }
    };

    // 获取URL参数
    const getUrlParam = (name) => {
        const reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        const r = window.location.search.substr(1).match(reg);
        if (r != null) return decodeURI(r[2]);
        return null;
    };

    // 加载联系人数据
    const loadContactData = (id) => {
        $.ajax({
            url: '/contacts/' + id,
            type: 'GET',
            success: res => {
                if (res.status === 200) {
                    // 填充表单
                    form.val('contactForm', res.data);
                }
            }
        });
    };

    // 处理表单提交
    const handleSubmit = (formData) => {
        const id = formData.id;
        const method = id ? 'PUT' : 'POST';
        
        $.ajax({
            url: '/contacts',
            type: method,
            contentType: 'application/json',
            data: JSON.stringify(formData),
            success: res => {
                if (res.status === 200) {
                    layer.msg(res.msg, {
                        icon: 1,
                        time: 2000
                    }, () => {
                        // 刷新父页面并关闭当前弹窗
                        window.parent.location.reload();
                        const index = parent.layer.getFrameIndex(window.name);
                        parent.layer.close(index);
                    });
                } else {
                    layer.msg(res.msg, {icon: 2});
                }
            }
        });
    };

    // 初始化
    init();
}); 