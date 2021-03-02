<!DOCTYPE html>
<html>
<head>
    <meta charset="utf‐8">
    <title>Hello World!</title>
</head>
<body>
Hello ${name}!
<br/>
遍历数据模型list中学生信息
<table>
    <tr>
        <td>序号</td>
        <td>姓名</td>
        <td>年龄</td>
        <td>金额</td>
    </tr>
    <#list stus as stu>
        <tr>
            <td>${stu_index}</td>
            <td>${stu.name}</td>
            <td>${stu.age}</td>
            <td>${stu.money}</td>
        </tr>
    </#list>
</table>
<br/>
遍历数据模型map数据
<br/>
姓名：${stuMap['stu1'].name}<br/>
<#list stuMap?keys as k>
    <tr>
        <td>
            姓名：${stuMap[k].name}<br/>
        </td>
        <td>
            年龄：${stuMap[k].age}<br/>
        </td>
    </tr>
</#list>

</body>
</html>