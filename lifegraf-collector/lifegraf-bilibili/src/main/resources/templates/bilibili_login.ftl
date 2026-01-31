<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>B站登录</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 800px;
            margin: 50px auto;
            padding: 20px;
        }
        .container {
            text-align: center;
        }
        .qrcode-container {
            margin: 20px 0;
        }
        .qrcode-img {
            border: 1px solid #ddd;
            padding: 10px;
            display: inline-block;
        }
        .status {
            margin: 20px 0;
            padding: 10px;
            border-radius: 5px;
        }
        .status.success {
            background-color: #d4edda;
            color: #155724;
        }
        .status.error {
            background-color: #f8d7da;
            color: #721c24;
        }
        .status.info {
            background-color: #d1ecf1;
            color: #0c5460;
        }
        button {
            padding: 10px 20px;
            margin: 5px;
            font-size: 16px;
            cursor: pointer;
        }
        .hidden {
            display: none;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>B站登录</h1>
        <div id="qrcodeSection">
            <button id="generateBtn" onclick="generateQRCode()">生成二维码</button>
            <div id="qrcodeContainer" class="qrcode-container hidden">
                <h3>请扫描二维码登录</h3>
                <div class="qrcode-img">
                    <img id="qrcodeImg" src="" alt="B站登录二维码" width="200" height="200">
                </div>
                <p>二维码有效期3分钟</p>
                <button id="checkBtn" onclick="checkLoginStatus()">检查登录状态</button>
            </div>
        </div>
        <div id="statusSection">
            <div id="statusInfo" class="status info hidden">等待操作...</div>
            <div id="cookieInfo" class="status success hidden"></div>
        </div>
    </div>

    <script>
        let qrcodeKey = null;

        async function generateQRCode() {
            try {
                showStatus('正在生成二维码...', 'info');

                const response = await fetch('/bilibili/qrcode');
                const data = await response.json();

                if (data.code === 0 && data.data) {
                    qrcodeKey = data.data.qrcode_key;
                    const qrUrl = data.data.url;

                    // 显示二维码（使用第三方API生成二维码图片）
                    document.getElementById('qrcodeImg').src = `https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=${encodeURIComponent(qrUrl)}`;
                    document.getElementById('qrcodeContainer').classList.remove('hidden');
                    document.getElementById('checkBtn').disabled = false;
                    showStatus('二维码生成成功，请在手机上扫描并确认登录', 'info');
                } else {
                    showStatus('生成二维码失败: ' + (data.message || '未知错误'), 'error');
                }
            } catch (error) {
                console.error('Error:', error);
                showStatus('生成二维码时发生错误: ' + error.message, 'error');
            }
        }

        async function checkLoginStatus() {
            if (!qrcodeKey) {
                showStatus('请先生成二维码', 'error');
                return;
            }

            document.getElementById('checkBtn').disabled = true;
            showStatus('正在检查登录状态...', 'info');

            try {
                const response = await fetch(`/bilibili/qrcode/poll?qrcode_key=${encodeURIComponent(qrcodeKey)}`);
                const data = await response.json();

                if (data.code === 8404402) {
                    // 未扫描
                    showStatus('二维码未扫描，请扫描后重试', 'info');
                    document.getElementById('checkBtn').disabled = false;
                } else if (data.code === 8404403) {
                    // 已扫描未确认
                    showStatus('二维码已扫描，请在手机上确认登录', 'info');
                    document.getElementById('checkBtn').disabled = false;
                } else if (data.code === 0) {
                    // 登录成功
                    showCookie(data.data);
                } else if (data.code === 400 || data.code === 8404401 || data.code === 8404101) {
                    // 二维码失效或超时
                    showStatus('二维码已失效，请重新生成', 'error');
                    document.getElementById('qrcodeContainer').classList.add('hidden');
                    qrcodeKey = null;
                } else {
                    // 其他错误
                    showStatus(data.message || '获取状态失败', 'error');
                    document.getElementById('checkBtn').disabled = false;
                }
            } catch (error) {
                console.error('Error checking status:', error);
                showStatus('检查状态时发生错误: ' + error.message, 'error');
                document.getElementById('checkBtn').disabled = false;
            }
        }

        function showStatus(message, type) {
            const statusElement = document.getElementById('statusInfo');
            statusElement.textContent = message;
            statusElement.className = `status ${type}`;
            statusElement.classList.remove('hidden');
        }

        function showCookie(cookie) {
            const cookieElement = document.getElementById('cookieInfo');
            cookieElement.textContent = `登录成功！Cookie: ${cookie}`;
            cookieElement.classList.remove('hidden');
            showStatus('登录成功！', 'success');
        }
    </script>
</body>
</html>