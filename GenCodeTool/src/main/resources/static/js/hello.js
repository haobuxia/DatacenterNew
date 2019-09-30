function gen(files) {

    var _author = $("#name").val();
    var _tableAnnotation = $("#tableAnnotation").val();
    var tableString = $("#tableNames").val().split(",");
    var _tableName = tableString[1];
    var _diskPath = $("#diskPath").val();
    var _packageName = $("#packageName").val();
    var _password = $("#password").val();
    var _url = $("#url").val();
    var _user = $("#user").val();
    var _modelName = $("#modelName").val();
    $.post("http://192.168.18.252:19081/data/object/attribute/list",
        {
            resId: tableString[0],
            pageInfo: {
                page: 0,
                pageSize: 0
            },
            page:1
        },
        function (resp) {
            if (resp.success) {
                $.post("/generate/code/res",
                    {
                        resId: tableString[0],
                        suffixs: files,
                        author: _author,
                        tableAnnotation: _tableAnnotation,
                        tableName: _tableName,
                        diskPath: _diskPath,
                        packageName: _packageName,
                        password: _password,
                        url: _url,
                        user: _user,
                        modelName: _modelName,
                        isDic: tableString[2],
                        objectList: JSON.stringify(resp.data)
                    },
                    function (resp) {
                        if (resp.success) {
                            showAlert("生成代码成功");
                        } else {
                            showAlert("生成失败");
                        }
                    })
            } else {
                showAlert("生成失败");
            }
        });
}

function tables() {
    var selectObj = $("#tableNames");
    $.get("http://192.168.18.252:19081/data/object/list",
        {
            dataObjectId: '',
            keyword: '',
            pageInfo: {
                page: 1,
                pageSize: 100
            }
        },
        function (resp) {
            if (resp.success) {
                selectObj.html('');
                $.each(resp.data.list, function (idx, table) {
                    console.log(resp);
                    selectObj.append('<option value="' + table.id + "," + table.defined + "," + table.isDic + '">' + table.name + '</option>');
                });
                $("#more").attr("style", "display:block;");
            } else {

            }
        })
}

function checks() {
    var test = $("input[name='checkbox']:checked");
    var checkBoxValue = "";
    if (test.val() == undefined) {
        showAlert("请选择！");
        return;
    }
    test.each(function () {
        checkBoxValue += $(this).val() + ",";
    });

    checkBoxValue = checkBoxValue.substring(0, checkBoxValue.length - 1);

    gen(checkBoxValue);
}

function initClick() {
    $("#add-table").off('click').click(function () {
        tables();
    });
    $("#add-button").off('click').click(function () {
        checks();
    });
}

$(function () {
    initClick();
});
