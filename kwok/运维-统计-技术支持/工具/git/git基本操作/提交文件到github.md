#### 提交本地文件到github

~~~
1. 进入要上传的文件夹目录中右击进入shell页面

2. 初始化本地仓库 git init

3. 查看当前文件状态：git status

4. 提交所有文件到暂存区：git add .

5. 提交所有文件到本地仓库：git commit -m '备注'

6. 配置全局用户名和全局邮箱：
    git config --global user.name "用户名"
    git config --global user.email "你注册的邮箱"
    
7. 添加远程登录 ：git remote add origin 远程仓库地址(https)
   如果之前添加过origin 执行命令：git remote rm origin

8. git push -u origin master (这里有一点需要注意一下：就是在上面创建GitHub仓库的时候，如果你勾选了Initialize this repository with a README（就是创建仓库的时候自动给你创建一个README文件），那么到了将本地仓库内容推送到GitHub仓库的时候就会报一个error: failed to push some refs to,这是由于新创建的那个仓库里面的README文件不在本地仓库目录中，这时可以通过以下命令先将内容合并一下：git pull --rebase origin master)
   
   然后再执行git push -u origin master

~~~