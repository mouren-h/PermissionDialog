document.addEventListener('DOMContentLoaded', () => {
    // 平滑滚动 (针对锚点链接)
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                const headerOffset = 60;
                const elementPosition = target.getBoundingClientRect().top;
                const offsetPosition = elementPosition + window.pageYOffset - headerOffset;

                window.scrollTo({
                    top: offsetPosition,
                    behavior: 'smooth'
                });
            }
        });
    });

    // 代码一键复制功能 (适配移动端触控反馈)
    const copyButtons = document.querySelectorAll('.copy-btn');
    
    copyButtons.forEach(button => {
        button.addEventListener('click', async () => {
            const targetId = button.getAttribute('data-target');
            const codeBlock = document.getElementById(targetId);
            
            if (codeBlock) {
                const textToCopy = codeBlock.innerText;
                
                try {
                    await navigator.clipboard.writeText(textToCopy);
                    
                    // 交互反馈
                    const originalHTML = button.innerHTML;
                    button.innerHTML = '<i class="ph ph-check" style="color: #4CAF50;"></i>';
                    
                    setTimeout(() => {
                        button.innerHTML = originalHTML;
                    }, 2000);
                } catch (err) {
                    console.error('复制失败:', err);
                    // 兼容非 HTTPS 环境的降级方案
                    const textArea = document.createElement('textarea');
                    textArea.value = textToCopy;
                    document.body.appendChild(textArea);
                    textArea.select();
                    try {
                        document.execCommand('copy');
                        button.innerHTML = '<i class="ph ph-check" style="color: #4CAF50;"></i>';
                        setTimeout(() => { button.innerHTML = originalHTML; }, 2000);
                    } catch (e) {
                        button.innerHTML = '失败';
                    }
                    document.body.removeChild(textArea);
                }
            }
        });
    });
});