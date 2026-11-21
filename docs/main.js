document.addEventListener('DOMContentLoaded', () => {
    // 平滑滚动
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                const headerOffset = 70; // 导航栏高度
                const elementPosition = target.getBoundingClientRect().top;
                const offsetPosition = elementPosition + window.pageYOffset - headerOffset;

                window.scrollTo({
                    top: offsetPosition,
                    behavior: 'smooth'
                });
            }
        });
    });

    // 代码一键复制功能
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
                    const originalText = button.innerHTML;
                    button.innerHTML = '<i class="ph ph-check"></i> 已复制!';
                    button.style.color = '#4CAF50';
                    
                    setTimeout(() => {
                        button.innerHTML = originalText;
                        button.style.color = 'white';
                    }, 2000);
                } catch (err) {
                    console.error('复制失败:', err);
                    button.innerHTML = '复制失败';
                }
            }
        });
    });

    // 滚动出现动画 (Intersection Observer)
    const observerOptions = {
        threshold: 0.1
    };

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
                observer.unobserve(entry.target);
            }
        });
    }, observerOptions);

    // 给卡片和区块加初始透明度并观察
    document.querySelectorAll('.card, .code-container, .preview-img-wrapper').forEach(el => {
        el.style.opacity = '0';
        el.style.transform = 'translateY(20px)';
        el.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
        observer.observe(el);
    });
});