// ============================================================
// CLOUDCOMPARE AI - FRONTEND JAVASCRIPT
// ============================================================

// Configuration - API Base URL
const API_BASE_URL = window.location.origin === 'null' || window.location.origin === 'file://' || window.location.port === '5000' ? 'http://localhost:5000' : (window.location.origin || 'http://localhost:5000');

// Global chart instances
let costChartInstance = null;
let performanceChartInstance = null;
let rankingChartInstance = null;
let distributionChartInstance = null;
let valueChartInstance = null;
let popularityChartInstance = null;
let trendChartInstance = null;
let aiRatingChartInstance = null;

// Track current services data for sorting
let currentServices = [];

// Track all providers globally
let allProviders = [];

// Global chart options template
const globalChartOptions = {
    responsive: true,
    plugins: {
        legend: {
            display: false
        }
    },
    scales: {
        y: {
            beginAtZero: true,
            grid: {
                color: "rgba(212, 160, 23, 0.08)"
            },
            ticks: {
                color: "#a89968"
            }
        },
        x: {
            grid: {
                display: false
            },
            ticks: {
                color: "#a89968",
                font: {
                    size: 10
                }
            }
        }
    }
};

// Current category state
let currentCategory = 'compute';

// Platform colors for charts
const platformColors = {
    'AWS': '#FF9900',      // Amazon Orange
    'GCP': '#4285F4',      // Google Blue
    'Azure': '#0078D4',    // Microsoft Azure Blue
    'OCI': '#F80000',      // Oracle Red
    'Alibaba': '#FF6A00'   // Alibaba Orange
};

// Platform icons
const platformIcons = {
    'AWS': 'fab fa-aws',
    'GCP': 'fab fa-google',
    'Azure': 'fab fa-microsoft',
    'OCI': 'fas fa-cloud',
    'Alibaba': 'fas fa-server'
};

// ============================================================
// BRANDING: Provider + Service logos (external URLs)
// NOTE: These are purely for UI rendering (no functional impact).
// If a mapping is missing, we simply don't render the <img>.
// ============================================================
const providerLogoUrlMap = {
    'AWS': 'assets/logos/aws.png',
    'GCP': 'assets/logos/gcp.png',
    'Azure': 'assets/logos/azure.png',
    'OCI': 'assets/logos/oci.png',
    'Alibaba': 'assets/logos/alibaba.png'
};

function getProviderLogoUrl(provider) {
    return providerLogoUrlMap?.[provider] || null;
}

// Service-level logos: map by (platform + known service keyword)
// We keep it intentionally partial; unknown services won't render a logo.
const serviceLogoUrlMap = {
    'AWS': {
        'EC2': 'https://upload.wikimedia.org/wikipedia/commons/7/7e/Amazon_EC2_Logo.svg',
        'S3': 'https://upload.wikimedia.org/wikipedia/commons/9/9a/Amazon_S3_Logo.svg',
        'RDS': 'https://upload.wikimedia.org/wikipedia/commons/a/a8/Amazon_RDS_Logo.svg',
        'Lambda': 'https://upload.wikimedia.org/wikipedia/commons/9/9f/AWS_Lambda_Logo.svg',
        'EKS': 'https://upload.wikimedia.org/wikipedia/commons/0/0e/Amazon_EKS_logo.svg',
        'ECS': 'https://upload.wikimedia.org/wikipedia/commons/2/25/Amazon_ECS_Logo.svg'
    },
    'GCP': {
        'Compute Engine': 'https://upload.wikimedia.org/wikipedia/commons/3/3c/Google_Compute_Engine_logo.svg',
        'Cloud Run': 'https://upload.wikimedia.org/wikipedia/commons/7/70/Cloud_Run_Logo.svg',
        'Cloud Storage': 'https://upload.wikimedia.org/wikipedia/commons/0/05/Google_Cloud_Storage_Logo.svg',
        'BigQuery': 'https://upload.wikimedia.org/wikipedia/commons/5/5c/BigQuery_Logo.svg'
    },
    'Azure': {
        'Virtual Machines': 'https://upload.wikimedia.org/wikipedia/commons/4/4f/Microsoft_Azure_Virtual_Machines_logo.svg',
        'SQL Database': 'https://upload.wikimedia.org/wikipedia/commons/6/6d/Microsoft_Azure_SQL_Database_logo.svg',
        'AKS': 'https://upload.wikimedia.org/wikipedia/commons/0/0c/Azure_Kubernetes_Service_logo.svg'
    },
    'OCI': {
        'Compute': 'https://upload.wikimedia.org/wikipedia/commons/9/9a/Oracle_Cloud_Infrastructure_logo.svg',
        'Object Storage': 'https://upload.wikimedia.org/wikipedia/commons/5/52/Oracle_Object_Storage_logo.svg'
    },
    'Alibaba': {
        'ECS': 'https://upload.wikimedia.org/wikipedia/commons/9/9e/Alibaba_Cloud_ECS_logo.svg',
        'OSS': 'https://upload.wikimedia.org/wikipedia/commons/0/00/Alibaba_Cloud_OSS_logo.svg'
    }
};

function getServiceLogoUrl(platform, serviceName) {
    const map = serviceLogoUrlMap?.[platform];
    if (!map || !serviceName) return null;

    const text = serviceName.toLowerCase();

    for (const [keyword, url] of Object.entries(map)) {
        if (!keyword) continue;
        if (text.includes(keyword.toLowerCase())) return url;
    }

    return null;
}

function renderLogoImg({ url, alt, sizePx = 18 }) {
    if (!url) return '';
    return `<img src="${url}" alt="${alt || ''}" loading="lazy" style="width:${sizePx}px;height:${sizePx}px;object-fit:contain;display:inline-block;vertical-align:middle;filter: drop-shadow(0 0 6px rgba(0,0,0,0.25));" onerror="this.style.display='none';">`;
}



// ============================================================
// SERVICE URL MAPPING — Real cloud provider product pages
// ============================================================
const serviceUrlMap = {
    // ---- AWS ----
    'AWS': {
        // Compute
        'EC2':            'https://aws.amazon.com/ec2/',
        'Lambda':         'https://aws.amazon.com/lambda/',
        'ECS':            'https://aws.amazon.com/ecs/',
        'EKS':            'https://aws.amazon.com/eks/',
        'Lightsail':      'https://aws.amazon.com/lightsail/',
        'Fargate':        'https://aws.amazon.com/fargate/',
        // Storage
        'S3':             'https://aws.amazon.com/s3/',
        'EBS':            'https://aws.amazon.com/ebs/',
        'EFS':            'https://aws.amazon.com/efs/',
        'Glacier':        'https://aws.amazon.com/s3/storage-classes/glacier/',
        // Database
        'RDS':            'https://aws.amazon.com/rds/',
        'DynamoDB':       'https://aws.amazon.com/dynamodb/',
        'Aurora':         'https://aws.amazon.com/rds/aurora/',
        'Redshift':       'https://aws.amazon.com/redshift/',
        'ElastiCache':    'https://aws.amazon.com/elasticache/',
        // AI
        'SageMaker':      'https://aws.amazon.com/sagemaker/',
        'Bedrock':        'https://aws.amazon.com/bedrock/',
        'Rekognition':    'https://aws.amazon.com/rekognition/',
        'Comprehend':     'https://aws.amazon.com/comprehend/',
        '_default':       'https://aws.amazon.com/'
    },
    // ---- GCP ----
    'GCP': {
        'Compute Engine':       'https://cloud.google.com/compute',
        'Cloud Functions':      'https://cloud.google.com/functions',
        'Cloud Run':            'https://cloud.google.com/run',
        'GKE':                  'https://cloud.google.com/kubernetes-engine',
        'Cloud Storage':        'https://cloud.google.com/storage',
        'Persistent Disk':      'https://cloud.google.com/persistent-disk',
        'Filestore':            'https://cloud.google.com/filestore',
        'Cloud SQL':            'https://cloud.google.com/sql',
        'Firestore':            'https://cloud.google.com/firestore',
        'BigQuery':             'https://cloud.google.com/bigquery',
        'Cloud Spanner':        'https://cloud.google.com/spanner',
        'Bigtable':             'https://cloud.google.com/bigtable',
        'Vertex AI':            'https://cloud.google.com/vertex-ai',
        'Gemini':               'https://cloud.google.com/gemini',
        'AutoML':               'https://cloud.google.com/automl',
        '_default':             'https://cloud.google.com/'
    },
    // ---- Azure ----
    'Azure': {
        'Virtual Machines':     'https://azure.microsoft.com/en-us/products/virtual-machines',
        'Functions':            'https://azure.microsoft.com/en-us/products/functions',
        'AKS':                  'https://azure.microsoft.com/en-us/products/kubernetes-service',
        'Container Instances':  'https://azure.microsoft.com/en-us/products/container-instances',
        'Blob Storage':         'https://azure.microsoft.com/en-us/products/storage/blobs',
        'Disk Storage':         'https://azure.microsoft.com/en-us/products/storage/disks',
        'Files':                'https://azure.microsoft.com/en-us/products/storage/files',
        'SQL Database':         'https://azure.microsoft.com/en-us/products/azure-sql/database',
        'Cosmos DB':            'https://azure.microsoft.com/en-us/products/cosmos-db',
        'Database for MySQL':   'https://azure.microsoft.com/en-us/products/mysql',
        'Database for PostgreSQL': 'https://azure.microsoft.com/en-us/products/postgresql',
        'Synapse':              'https://azure.microsoft.com/en-us/products/synapse-analytics',
        'Azure AI':             'https://azure.microsoft.com/en-us/products/ai-services',
        'OpenAI Service':       'https://azure.microsoft.com/en-us/products/ai-services/openai-service',
        'Machine Learning':     'https://azure.microsoft.com/en-us/products/machine-learning',
        '_default':             'https://azure.microsoft.com/'
    },
    // ---- OCI (Oracle Cloud) ----
    'OCI': {
        'Compute':              'https://www.oracle.com/cloud/compute/',
        'Functions':            'https://www.oracle.com/cloud/cloud-native/functions/',
        'OKE':                  'https://www.oracle.com/cloud/cloud-native/container-engine-kubernetes/',
        'Object Storage':       'https://www.oracle.com/cloud/storage/object-storage/',
        'Block Volume':         'https://www.oracle.com/cloud/storage/block-volumes/',
        'File Storage':         'https://www.oracle.com/cloud/storage/file-storage/',
        'Autonomous Database':  'https://www.oracle.com/autonomous-database/',
        'MySQL':                'https://www.oracle.com/mysql/',
        'NoSQL':                'https://www.oracle.com/database/nosql/',
        'AI Services':          'https://www.oracle.com/artificial-intelligence/',
        'Generative AI':        'https://www.oracle.com/artificial-intelligence/generative-ai/',
        '_default':             'https://www.oracle.com/cloud/'
    },
    // ---- Alibaba Cloud ----
    'Alibaba': {
        'ECS':                  'https://www.alibabacloud.com/product/ecs',
        'Function Compute':     'https://www.alibabacloud.com/product/function-compute',
        'ACK':                  'https://www.alibabacloud.com/product/kubernetes',
        'OSS':                  'https://www.alibabacloud.com/product/object-storage-service',
        'NAS':                  'https://www.alibabacloud.com/product/nas',
        'Block Storage':        'https://www.alibabacloud.com/product/disk',
        'ApsaraDB RDS':        'https://www.alibabacloud.com/product/apsaradb-for-rds',
        'PolarDB':              'https://www.alibabacloud.com/product/polardb',
        'AnalyticDB':           'https://www.alibabacloud.com/product/analyticdb-for-mysql',
        'PAI':                  'https://www.alibabacloud.com/product/machine-learning',
        'Tongyi':               'https://www.alibabacloud.com/product/tongyi',
        '_default':             'https://www.alibabacloud.com/'
    }
};

// AI Tool URLs — direct product pages
const aiToolUrlMap = {
    'ChatGPT':      'https://chat.openai.com/',
    'Claude':       'https://claude.ai/',
    'Gemini':       'https://gemini.google.com/',
    'Copilot':      'https://copilot.microsoft.com/',
    'Perplexity':   'https://www.perplexity.ai/',
    'Midjourney':   'https://www.midjourney.com/',
    'DALL-E':       'https://openai.com/dall-e-3',
    'Stable Diffusion': 'https://stability.ai/',
    'Runway':       'https://runwayml.com/',
    'Jasper':       'https://www.jasper.ai/',
    'Notion AI':    'https://www.notion.so/product/ai',
    'GitHub Copilot': 'https://github.com/features/copilot',
    'DeepCode':     'https://www.deepcode.ai/',
    'Kite':         'https://www.kite.com/',
    'TabNine':      'https://www.tabnine.com/',
    'Repl.it':      'https://replit.com/site/ghostwriter',
    'Cursor':       'https://cursor.sh/',
    'Suno':         'https://suno.com/',
    'ElevenLabs':   'https://elevenlabs.io/',
    'Gamma':        'https://gamma.app/',
    'Tome':         'https://tome.app/',
    'Julius AI':    'https://julius.ai/'
};

/**
 * Resolves the best URL for a given cloud service.
 * Matches service_name against known keywords for the provider.
 */
function getServiceUrl(platform, serviceName) {
    const providerMap = serviceUrlMap[platform];
    if (!providerMap) return null;

    // Try exact match first
    for (const [keyword, url] of Object.entries(providerMap)) {
        if (keyword === '_default') continue;
        if (serviceName && serviceName.toLowerCase().includes(keyword.toLowerCase())) {
            return url;
        }
    }

    // Fallback to provider default
    return providerMap['_default'] || null;
}

/**
 * Resolves the best URL for a given AI tool.
 */
function getAiToolUrl(toolName) {
    if (!toolName) return null;
    for (const [name, url] of Object.entries(aiToolUrlMap)) {
        if (toolName.toLowerCase().includes(name.toLowerCase())) {
            return url;
        }
    }
    // Fallback: Return a Google search link for the tool
    return `https://www.google.com/search?q=${encodeURIComponent(toolName + " AI Tool")}`;
}

// Category configuration
const categoryConfig = {
    compute: {
        icon: 'fa-microchip',
        label: 'Compute',
        description: 'Virtual machines and containers'
    },
    storage: {
        icon: 'fa-database',
        label: 'Storage',
        description: 'Object and block storage'
    },
    database: {
        icon: 'fa-server',
        label: 'Database',
        description: 'Managed database services'
    }
};

// Initialize on page load
document.addEventListener("DOMContentLoaded", function () {
    console.log("CloudCompare AI initialized");

    // Set up category button listeners
    setupCategoryButtons();

    // Set up enter key support
    setupEnterKeySupport();

    // Load service count
    loadServiceCount();

    // Load regions dynamically
    loadRegions();

    // Check Auth State
    checkAuthState();

    // Load service types for default category
    loadServiceTypes(currentCategory);
    
    // EXCELLENCE: Initialize results filters
    setupProviderFilters();

    // Dashbaord Stats Badge Interactions
    const serviceBadge = document.getElementById('serviceCountBadge');
    const providerBadge = document.getElementById('providerCountBadge');

    if (serviceBadge) {
        serviceBadge.onclick = async () => {
            showKnowledgeModal('Available Service Types', await getAllServiceTypesHtml());
        };
    }

    if (providerBadge) {
        providerBadge.onclick = () => {
            showKnowledgeModal('Supported Cloud Providers', getProvidersHtml());
        };
    }

    // PHASE 1: OVER EXCELLENCE - Global Pulse on load
    // Automatically perform a global compute comparison to populate the dashboard immediately
    setTimeout(() => {
        const categoryBtn = document.querySelector('.category-btn[data-category="compute"]');
        if (categoryBtn) {
            categoryBtn.click(); // Select compute
            compare(); // Trigger analysis
        }
    }, 1500);
});

function showKnowledgeModal(title, content) {
    const modal = document.getElementById('knowledgeModal');
    document.getElementById('modalTitle').innerHTML = `<i class="fas fa-info-circle"></i> ${title}`;
    document.getElementById('modalBody').innerHTML = content;
    modal.style.display = 'flex';
    
    // Close on overlay click
    modal.onclick = (e) => {
        if (e.target === modal) closeKnowledgeModal();
    };
}

function closeKnowledgeModal() {
    document.getElementById('knowledgeModal').style.display = 'none';
}

async function getAllServiceTypesHtml() {
    const categories = ['compute', 'storage', 'database', 'ai'];
    let html = '<div class="knowledge-grid">';
    
    for (const cat of categories) {
        try {
            const res = await fetch(`${API_BASE_URL}/api/service-types/${cat}`);
            if (res.ok) {
                const result = await res.json();
                const types = result.data || [];
                html += `
                    <div class="knowledge-category">
                        <h4><i class="fas ${getCategoryIcon(cat)}"></i> ${cat.toUpperCase()}</h4>
                        <ul>
                            ${types.map(t => `<li><i class="fas fa-check-circle"></i> ${t.label || t.name}</li>`).join('')}
                        </ul>
                    </div>
                `;
            }
        } catch (e) { console.error(e); }
    }
    html += '</div>';
    return html;
}

function getProvidersHtml() {
    const providers = [
        { name: 'AWS', desc: 'Amazon Web Services - Global Leader', icon: 'fab fa-aws' },
        { name: 'GCP', desc: 'Google Cloud Platform - AI & Data Expert', icon: 'fab fa-google' },
        { name: 'Azure', desc: 'Microsoft Azure - Enterprise Favorite', icon: 'fab fa-microsoft' },
        { name: 'OCI', desc: 'Oracle Cloud - High Performance Database', icon: 'fas fa-cloud' },
        { name: 'Alibaba', desc: 'Alibaba Cloud - Leading Asia Provider', icon: 'fas fa-server' }
    ];
    
    return `
        <div class="knowledge-grid">
            ${providers.map(p => `
                <div class="knowledge-item">
                    <i class="${p.icon}" style="font-size: 2rem; color: #d4a017; margin-bottom: 1rem;"></i>
                    <h5>${p.name}</h5>
                    <p>${p.desc}</p>
                </div>
            `).join('')}
        </div>
    `;
}

function getCategoryIcon(cat) {
    const icons = { compute: 'fa-microchip', storage: 'fa-hdd', database: 'fa-server', ai: 'fa-brain' };
    return icons[cat] || 'fa-cube';
}

// Check auth state
function checkAuthState() {
    const token = localStorage.getItem('token');
    const userName = localStorage.getItem('userName');

    const loginLinkBtn = document.getElementById('loginLinkBtn');
    const logoutBtn = document.getElementById('logoutBtn');

    if (token && userName) {
        if (loginLinkBtn) loginLinkBtn.style.display = 'none';
        if (logoutBtn) {
            logoutBtn.style.display = 'inline-block';
            logoutBtn.innerHTML = `<i class="fas fa-sign-out-alt"></i> ${userName}`;
            logoutBtn.onclick = function () {
                localStorage.removeItem('token');
                localStorage.removeItem('userName');
                window.location.href = 'login.html';
            };
        }
    } else {
        if (loginLinkBtn) loginLinkBtn.style.display = 'inline-block';
        if (logoutBtn) logoutBtn.style.display = 'none';
    }
}

// Load regions from API
async function loadRegions() {
    try {
        const res = await fetch(`${API_BASE_URL}/api/regions`);
        if (res.ok) {
            const result = await res.json();
            const regions = result.data || [];
            const regionSelect = document.getElementById('region');
            if (!regionSelect) return;

            // Keep the first "All Regions" option
            regionSelect.innerHTML = '<option value="all">All Regions</option>';

            // Add regions from database
            regions.forEach(region => {
                const option = document.createElement('option');
                option.value = region.value;
                option.textContent = region.label;
                regionSelect.appendChild(option);
            });

            console.log("Loaded " + regions.length + " regions");
        }
    } catch (error) {
        console.log("Could not load regions");
    }
}

// Load service types dynamically based on category
async function loadServiceTypes(category) {
    try {
        const res = await fetch(`${API_BASE_URL}/api/service-types/${category}`);
        if (res.ok) {
            const result = await res.json();
            const types = result.data || [];
            const select = document.getElementById('serviceTypeSelect');
            if (!select) return;

            select.innerHTML = '<option value="all">All Types</option>';

            types.forEach(type => {
                const option = document.createElement('option');
                option.value = type.value || type.id;
                option.textContent = type.label || type.name;
                select.appendChild(option);
            });
        }
    } catch (error) {
        console.error('Error loading service types:', error);
    }
}

// Setup category buttons
function setupCategoryButtons() {
    const buttons = document.querySelectorAll('.category-btn');
    buttons.forEach(btn => {
        btn.addEventListener('click', function () {
            // Remove active from all
            buttons.forEach(b => b.classList.remove('active'));
            // Add active to clicked
            this.classList.add('active');
            // Update current category
            currentCategory = this.dataset.category;
            // Load specific service types for this category
            loadServiceTypes(currentCategory);
        });
    });
}

// Setup enter key support
function setupEnterKeySupport() {
    const inputs = document.querySelectorAll('.input-group input, .input-group select');
    inputs.forEach(input => {
        input.addEventListener('keypress', function (e) {
            if (e.key === 'Enter') {
                compare();
            }
        });
    });
}

// Load service count and provider count
async function loadServiceCount() {
    // No static DB anymore — show accurate counts from hardcoded service types
    const totalServiceTypes = 12 + 9 + 13 + 20; // compute + storage + database + ai
    document.getElementById('serviceCount').textContent = totalServiceTypes + ' Service Types';
    document.getElementById('featureServiceCount').textContent = totalServiceTypes + ' Cloud Service Types';
    document.getElementById('providerCount').textContent = '5 Providers';
    document.getElementById('featureProviderCount').textContent = '5 Cloud Providers';
    allProviders = ['AWS', 'GCP', 'Azure', 'OCI', 'Alibaba'];
}

// Main compare function
let isComparing = false;
async function compare() {
    if (isComparing) return;

    // Get current category from active button
    const activeBtn = document.querySelector('.category-btn.active');
    const category = activeBtn ? activeBtn.dataset.category : 'compute';

    const compareBtn = document.querySelector('.compare-btn');
    const originalBtnText = compareBtn.innerHTML;
    compareBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Analyzing...';
    compareBtn.style.opacity = '0.7';
    compareBtn.style.cursor = 'not-allowed';
    isComparing = true;

    // Show loading state
    showLoading();

    // Get form values
    const regionValue = document.getElementById("region").value;
    const priorityValue = document.getElementById("priority").value;
    let currentType = 'all';
    const typeSelect = document.getElementById('serviceTypeSelect');
    if (typeSelect) {
        currentType = typeSelect.value;
    }

    const payload = {
        category: category,
        region: regionValue || 'all',
        priority: priorityValue || 'balanced',
        serviceType: currentType || 'all',
        cpu: parseInt(document.getElementById("cpu").value) || 2,
        ram: parseInt(document.getElementById("ram").value) || 4,
        storage: parseInt(document.getElementById("storage").value) || 100,
        hours: parseInt(document.getElementById("hours").value) || 730
    };

    try {
        const token = localStorage.getItem('token');
        const headers = {
            "Content-Type": "application/json"
        };
        if (token) {
            headers["Authorization"] = `Bearer ${token}`;
        }

        const res = await fetch(`${API_BASE_URL}/api/compare`, {
            method: "POST",
            headers: headers,
            body: JSON.stringify(payload)
        });

        if (!res.ok) {
            if (res.status === 401 || res.status === 403) {
                // Do not redirect, just clear local storage if invalid token was sent
                localStorage.removeItem('token');
                localStorage.removeItem('userName');
            }
            throw new Error(`HTTP error! status: ${res.status}`);
        }

        const result = await res.json();

        if (!result.success || result.error) {
            throw new Error(result.error || "Comparison failed");
        }

        const apiData = result.data;
        const services = apiData.services || [];

        if (services.length === 0) {
            showError("No services found for the selected category");
            return;
        }

        hideLoading();

        // Store globally for sorting
        currentServices = services;

        // Display results
        displayRecommendations(services);
        displayCharts(services);
        displayProviderStats(apiData.providerStats);
        displayTable(services);

        // Show results section
        document.getElementById('resultsSection').style.display = 'block';
        document.getElementById('emptyState').style.display = 'none';
        
        // PHASE 2: AI Architect Summary
        generateAiSummary(services);

    } catch (error) {
        console.error("Compare error:", error);
        hideLoading();
        showError("Failed to compare services. Please try again.\n" + error.message);
    } finally {
        isComparing = false;
        const compareBtn = document.querySelector('.compare-btn');
        compareBtn.innerHTML = '<i class="fas fa-rocket"></i> Compare Services';
        compareBtn.style.opacity = '1';
        compareBtn.style.cursor = 'pointer';
    }
}

// Display recommendations grid
function displayRecommendations(services) {
    const grid = document.getElementById('recommendationsGrid');

    if (!services || services.length === 0) {
        grid.style.display = 'none';
        return;
    }

    grid.innerHTML = '';

    // Sort services by score to ensure they are displayed in priority order
    const sortedServices = [...services].sort((a, b) => b.score - a.score);

    sortedServices.forEach((rec, index) => {
        const platformIcon = platformIcons[rec.platform] || 'fas fa-cloud';
        const color = platformColors[rec.platform] || '#3b82f6';

        // Use gold, silver, bronze for top 3, else standard color
        let rankBadgeStyle = `background: ${adjustColorOpacity(color, 0.15)}; color: ${color}; border: 1px solid ${adjustColorOpacity(color, 0.3)};`;
        let rankText = `#${index + 1} Recommendation`;

        if (index === 0) rankBadgeStyle = `background: linear-gradient(135deg, rgba(251, 191, 36, 0.2), rgba(245, 158, 11, 0.2)); color: #fbbf24; border: 1px solid rgba(251, 191, 36, 0.5);`; // Gold
        else if (index === 1) rankBadgeStyle = `background: linear-gradient(135deg, rgba(148, 163, 184, 0.2), rgba(100, 116, 139, 0.2)); color: #cbd5e1; border: 1px solid rgba(148, 163, 184, 0.5);`; // Silver
        else if (index === 2) rankBadgeStyle = `background: linear-gradient(135deg, rgba(205, 127, 50, 0.2), rgba(184, 115, 51, 0.2)); color: #cd7f32; border: 1px solid rgba(205, 127, 50, 0.5);`; // Bronze

        // Resolve the real cloud provider URL for this service
        const serviceUrl = getServiceUrl(rec.platform, rec.service_name);
        const visitBtnHtml = serviceUrl
            ? `<a href="${serviceUrl}" target="_blank" rel="noopener noreferrer" class="visit-service-btn" style="border-color: ${adjustColorOpacity(color, 0.5)}; color: ${color};">
                   <i class="fas fa-external-link-alt"></i> Visit Service
               </a>`
            : '';

        const card = document.createElement('div');
        card.className = 'recommendation-card';
        card.innerHTML = `
            <div class="recommendation-badge" style="${rankBadgeStyle}">
                <i class="${platformIcon}"></i> ${rankText}
            </div>
            <div class="recommendation-content">
                <div class="recommendation-platform">
                    <h3 style="display:flex; align-items:center; gap:0.6rem; flex-wrap:wrap;">
                        <span style="font-size:1.15rem; display:inline-flex; align-items:center; justify-content:center;">
                            ${renderLogoImg({ url: getProviderLogoUrl(rec.platform), alt: rec.platform, sizePx: 22 })}
                        </span>
                        <span style="display:inline-flex; align-items:center; gap:0.5rem; flex-wrap:wrap;">
                            <span>${rec.platform} -</span>
                            <span>${rec.service_name}</span>
                        </span>
                    </h3>
                </div>
                <div class="recommendation-stats">
                    <div class="stat-item" style="text-align: left;">
                        <span class="stat-label">Est. Cost</span>
                        <span class="stat-value cost" style="font-size: 1.25rem;">$${rec.cost.toFixed(2)}</span>
                    </div>
                    <div class="stat-item" style="text-align: right;">
                        <span class="stat-label">Performance</span>
                        <span class="stat-value performance" style="font-size: 1.25rem; color: ${color};">${rec.performanceLevel}</span>
                    </div>
                </div>
                ${visitBtnHtml}
            </div>
        `;
        grid.appendChild(card);
    });

    grid.style.display = 'grid';
}

// Generate gradient for bars based on platform color - Premium Financial Dashboard Style
function createGradient(ctx, color) {
    const canvasHeight = ctx.canvas.height || 250;
    const gradient = ctx.createLinearGradient(0, 0, 0, canvasHeight);
    // Subtle premium gradient: light at top fading to darker
    gradient.addColorStop(0, adjustColorBrightness(color, 1.25));
    gradient.addColorStop(0.35, adjustColorBrightness(color, 1.1));
    gradient.addColorStop(0.65, color);
    gradient.addColorStop(1, adjustColorBrightness(color, 0.5));
    return gradient;
}

// Create gradient with premium glow effect
function createGlowGradient(ctx, color) {
    const canvasHeight = ctx.canvas.height || 250;
    const gradient = ctx.createLinearGradient(0, canvasHeight, 0, 0);
    gradient.addColorStop(0, adjustColorOpacity(color, 0.15));
    gradient.addColorStop(0.4, adjustColorOpacity(color, 0.5));
    gradient.addColorStop(0.7, adjustColorOpacity(color, 0.8));
    gradient.addColorStop(1, adjustColorBrightness(color, 1.4));
    return gradient;
}

// Create soft shadow gradient for bars
function createShadowGradient(ctx, color) {
    const canvasHeight = ctx.canvas.height || 250;
    const gradient = ctx.createLinearGradient(0, canvasHeight * 0.7, 0, canvasHeight);
    gradient.addColorStop(0, adjustColorOpacity(color, 0));
    gradient.addColorStop(1, adjustColorOpacity(color, 0.35));
    return gradient;
}

// Adjust color brightness helper
function adjustColorBrightness(hex, factor) {
    const r = Math.min(255, Math.round(parseInt(hex.slice(1, 3), 16) * factor));
    const g = Math.min(255, Math.round(parseInt(hex.slice(3, 5), 16) * factor));
    const b = Math.min(255, Math.round(parseInt(hex.slice(5, 7), 16) * factor));
    return `rgb(${r}, ${g}, ${b})`;
}

// Adjust color opacity helper
function adjustColorOpacity(hex, opacity) {
    const r = parseInt(hex.slice(1, 3), 16);
    const g = parseInt(hex.slice(3, 5), 16);
    const b = parseInt(hex.slice(5, 7), 16);
    return `rgba(${r}, ${g}, ${b}, ${opacity})`;
}

// Custom easing function - easeOutCubic
function easeOutCubic(t) {
    return 1 - Math.pow(1 - t, 3);
}

// Custom easing function - easeOutQuart
function easeOutQuart(t) {
    return 1 - Math.pow(1 - t, 4);
}

// Enhanced chart options with smooth animations and clean design - Premium Financial Dashboard Style
function getChartOptions(title) {
    return {
        responsive: true,
        maintainAspectRatio: true,
        animation: {
            duration: 1500,
            easing: 'easeOutCubic',
            delay: function (context) {
                // Staggered animation on load for premium feel
                return context.dataIndex * 120;
            }
        },
        interaction: {
            mode: 'index',
            intersect: false,
        },
        hover: {
            mode: 'index',
            intersect: false,
            axis: 'x'
        },
        plugins: {
            legend: {
                display: false // Remove unnecessary legends for cleaner UI
            },
            tooltip: {
                enabled: true,
                backgroundColor: 'rgba(15, 23, 42, 0.95)',
                titleColor: '#ffffff',
                bodyColor: '#e2e8f0',
                borderColor: 'rgba(59, 130, 246, 0.4)',
                borderWidth: 1,
                padding: 14,
                cornerRadius: 12,
                displayColors: true,
                boxPadding: 8,
                titleFont: {
                    family: "'Inter', sans-serif",
                    size: 13,
                    weight: '600'
                },
                bodyFont: {
                    family: "'Inter', sans-serif",
                    size: 12,
                    weight: '500'
                },
                callbacks: {
                    label: function (context) {
                        return title + ': ' + context.parsed.y.toFixed(2);
                    }
                }
            }
        },
        scales: {
            y: {
                beginAtZero: true,
                grid: {
                    color: "rgba(255, 255, 255, 0.03)", // Subtle grid
                    drawBorder: false,
                    lineWidth: 1
                },
                ticks: {
                    color: "#64748b",
                    font: {
                        family: "'Inter', sans-serif",
                        size: 11,
                        weight: '500'
                    },
                    padding: 12,
                    backdropColor: 'transparent'
                },
                border: {
                    display: false
                }
            },
            x: {
                grid: {
                    display: false
                },
                ticks: {
                    color: "#64748b",
                    font: {
                        family: "'Inter', sans-serif",
                        size: 10,
                        weight: '500'
                    },
                    padding: 8,
                    maxRotation: 45
                },
                border: {
                    display: false
                }
            }
        }
    };
}

// Display charts
function displayCharts(services) {
    const labels = services.map(s => s.platform + ' ' + s.service_name.split(' ').slice(1).join(' '));
    const costs = services.map(s => s.cost);
    const performance = services.map(s => s.performance_score);
    const scores = services.map(s => s.score);
    const bgColors = services.map(s => platformColors[s.platform] || '#64748b');

    // Destroy existing charts
    if (costChartInstance) costChartInstance.destroy();
    if (performanceChartInstance) performanceChartInstance.destroy();
    if (rankingChartInstance) rankingChartInstance.destroy();
    if (distributionChartInstance) distributionChartInstance.destroy();
    if (valueChartInstance) valueChartInstance.destroy();
    if (popularityChartInstance) popularityChartInstance.destroy();
    if (trendChartInstance) trendChartInstance.destroy();

    // Get canvas contexts
    const costCtx = document.getElementById("costChart").getContext("2d");
    const perfCtx = document.getElementById("performanceChart").getContext("2d");
    const rankCtx = document.getElementById("rankingChart").getContext("2d");

    // Cost Chart with enhanced gradient and modern SaaS styling
    costChartInstance = new Chart(costCtx, {
        type: "bar",
        data: {
            labels: labels,
            datasets: [{
                label: "Cost ($)",
                data: costs,
                backgroundColor: bgColors.map(c => createGradient(costCtx, c)),
                borderColor: 'transparent',
                borderWidth: 0,
                borderRadius: 0, // Sharp square bars
                borderSkipped: false,
                hoverBackgroundColor: bgColors.map(c => createGlowGradient(costCtx, c)),
                hoverBorderWidth: 0,
                barPercentage: 0.7, // Consistent thickness
                categoryPercentage: 0.8 // Bar spacing
            }]
        },
        options: {
            ...getChartOptions("Cost"),
            plugins: {
                ...getChartOptions("Cost").plugins,
                tooltip: {
                    enabled: true,
                    backgroundColor: 'rgba(15, 23, 42, 0.95)',
                    titleColor: '#ffffff',
                    bodyColor: '#e2e8f0',
                    borderColor: 'rgba(59, 130, 246, 0.3)',
                    borderWidth: 1,
                    padding: 14,
                    cornerRadius: 10,
                    callbacks: {
                        label: function (context) {
                            return "Cost: $" + context.raw.toFixed(2);
                        }
                    }
                }
            }
        }
    });

    // Performance Chart with enhanced gradient and modern SaaS styling
    performanceChartInstance = new Chart(perfCtx, {
        type: "bar",
        data: {
            labels: labels,
            datasets: [{
                label: "Performance",
                data: performance,
                backgroundColor: bgColors.map(c => createGradient(perfCtx, c)),
                borderColor: 'transparent',
                borderWidth: 0,
                borderRadius: 0, // Sharp square bars
                borderSkipped: false,
                hoverBackgroundColor: bgColors.map(c => createGlowGradient(perfCtx, c)),
                hoverBorderWidth: 0,
                barPercentage: 0.7,
                categoryPercentage: 0.8
            }]
        },
        options: {
            ...getChartOptions("Performance"),
            scales: {
                ...getChartOptions("Performance").scales,
                y: {
                    ...getChartOptions("Performance").scales.y,
                    max: 10
                }
            },
            plugins: {
                ...getChartOptions("Performance").plugins,
                tooltip: {
                    enabled: true,
                    backgroundColor: 'rgba(15, 23, 42, 0.95)',
                    titleColor: '#ffffff',
                    bodyColor: '#e2e8f0',
                    borderColor: 'rgba(59, 130, 246, 0.3)',
                    borderWidth: 1,
                    padding: 14,
                    cornerRadius: 10,
                    callbacks: {
                        label: function (context) {
                            return "Performance: " + context.raw;
                        }
                    }
                }
            }
        }
    });

    // Ranking Chart with enhanced gradient and modern SaaS styling
    rankingChartInstance = new Chart(rankCtx, {
        type: "bar",
        data: {
            labels: labels,
            datasets: [{
                label: "Score",
                data: scores,
                backgroundColor: bgColors.map(c => createGradient(rankCtx, c)),
                borderColor: 'transparent',
                borderWidth: 0,
                borderRadius: 0, // Sharp square bars
                borderSkipped: false,
                hoverBackgroundColor: bgColors.map(c => createGlowGradient(rankCtx, c)),
                hoverBorderWidth: 0,
                barPercentage: 0.7,
                categoryPercentage: 0.8
            }]
        },
        options: {
            ...getChartOptions("Score"),
            scales: {
                ...getChartOptions("Score").scales,
                y: {
                    ...getChartOptions("Score").scales.y,
                    max: 10
                }
            },
            plugins: {
                ...getChartOptions("Score").plugins,
                tooltip: {
                    enabled: true,
                    backgroundColor: 'rgba(15, 23, 42, 0.95)',
                    titleColor: '#ffffff',
                    bodyColor: '#e2e8f0',
                    borderColor: 'rgba(59, 130, 246, 0.3)',
                    borderWidth: 1,
                    padding: 14,
                    cornerRadius: 10,
                    callbacks: {
                        label: function (context) {
                            return "Score: " + context.raw.toFixed(1);
                        }
                    }
                }
            }
        }
    });

    // Additional charts - Platform Comparison
    displayPlatformCharts(services);

    // Display Trend Line Chart - Stock Market Style
    displayTrendChart(services);
}

// Display Trend Line Chart - Stock Market Style
function displayTrendChart(services) {
    const trendCtx = document.getElementById("trendChart").getContext("2d");

    // Generate simulated trend data based on services
    // Create 12 months of trend data
    const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

    // Map over all services instead of just platforms
    const datasets = services.map((service, index) => {
        const baseCost = service.cost || 50;
        const variation = (index + 1) * 0.1;

        // Generate trend data with some variation
        const data = months.map((_, i) => {
            const randomVar = (Math.sin(i * 0.5 + index) * 0.15 + 1);
            const trend = baseCost * randomVar * (1 - variation * 0.1 + (i * 0.02));
            return parseFloat(trend.toFixed(2));
        });

        const color = platformColors[service.platform] || '#64748b';

        return {
            label: `${service.platform} - ${service.service_name}`,
            data: data,
            borderColor: color,
            backgroundColor: adjustColorOpacity(color, 0.1),
            borderWidth: 2,
            fill: false,
            tension: 0, // Sharp direct lines (no curves)
            pointRadius: 4,
            pointHoverRadius: 8,
            pointBackgroundColor: color,
            pointBorderColor: 'rgba(15, 23, 42, 0.9)',
            pointBorderWidth: 2,
            pointHoverBackgroundColor: color,
            pointHoverBorderColor: '#ffffff'
        };
    });

    trendChartInstance = new Chart(trendCtx, {
        type: "line",
        data: {
            labels: months,
            datasets: datasets
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            interaction: {
                mode: 'index',
                intersect: false,
                axis: 'x'
            },
            hover: {
                mode: 'index',
                intersect: false,
                axis: 'x'
            },
            animation: {
                duration: 1800,
                easing: 'easeOutCubic'
            },
            plugins: {
                legend: {
                    display: true,
                    position: 'top',
                    align: 'end',
                    labels: {
                        color: "#94a3b8",
                        padding: 24,
                        usePointStyle: true,
                        pointStyle: 'circle',
                        font: {
                            family: "'Inter', sans-serif",
                            size: 11,
                            weight: '500'
                        }
                    }
                },
                tooltip: {
                    enabled: true,
                    backgroundColor: 'rgba(15, 23, 42, 0.95)',
                    titleColor: '#ffffff',
                    bodyColor: '#e2e8f0',
                    borderColor: 'rgba(59, 130, 246, 0.4)',
                    borderWidth: 1,
                    padding: 16,
                    cornerRadius: 12,
                    mode: 'index',
                    intersect: false,
                    callbacks: {
                        title: function (context) {
                            return '📊 ' + context[0].label + ' 2026';
                        },
                        label: function (context) {
                            return context.dataset.label + ': $' + context.raw.toFixed(2);
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: false,
                    grid: {
                        color: "rgba(255, 255, 255, 0.025)",
                        drawBorder: false
                    },
                    ticks: {
                        color: "#64748b",
                        font: {
                            family: "'Inter', sans-serif",
                            size: 11,
                            weight: '500'
                        },
                        padding: 12,
                        callback: function (value) {
                            return '$' + value;
                        }
                    },
                    border: {
                        display: false
                    }
                },
                x: {
                    grid: {
                        display: false
                    },
                    ticks: {
                        color: "#64748b",
                        font: {
                            family: "'Inter', sans-serif",
                            size: 10,
                            weight: '500'
                        },
                        padding: 8
                    },
                    border: {
                        display: false
                    }
                }
            }
        }
    });
}

// Display additional platform comparison charts with enhanced styling
function displayPlatformCharts(services) {
    // Get platform-level aggregated data
    const platforms = [...new Set(services.map(s => s.platform))];
    const platformData = platforms.map(p => {
        const platformServices = services.filter(s => s.platform === p);
        return {
            platform: p,
            avgCost: platformServices.reduce((a, s) => a + s.cost, 0) / platformServices.length,
            avgPerformance: platformServices.reduce((a, s) => a + s.performance_score, 0) / platformServices.length,
            avgPopularity: platformServices.reduce((a, s) => a + (s.popularity_score || 5), 0) / platformServices.length,
            avgScore: platformServices.reduce((a, s) => a + s.score, 0) / platformServices.length,
            count: platformServices.length
        };
    });

    const platformLabels = platformData.map(p => p.platform);
    const platformBgColors = platformData.map(p => platformColors[p.platform] || '#64748b');

    // Get canvas contexts
    const distCtx = document.getElementById("distributionChart").getContext("2d");
    const valueCtx = document.getElementById("valueChart").getContext("2d");
    const popCtx = document.getElementById("popularityChart").getContext("2d");

    // Distribution Doughnut Chart with premium styling
    distributionChartInstance = new Chart(distCtx, {
        type: "doughnut",
        data: {
            labels: platformLabels,
            datasets: [{
                data: platformData.map(p => p.count),
                backgroundColor: platformBgColors.map(c => adjustColorOpacity(c, 0.85)),
                borderColor: "rgba(15, 23, 42, 0.95)",
                borderWidth: 4,
                hoverOffset: 16,
                hoverBorderColor: "rgba(59, 130, 246, 0.9)",
                hoverBorderWidth: 4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            animation: {
                duration: 1600,
                easing: 'easeOutCubic',
                animateRotate: true,
                animateScale: true
            },
            cutout: '60%',
            plugins: {
                legend: {
                    display: true,
                    position: 'bottom',
                    labels: {
                        color: "#94a3b8",
                        padding: 20,
                        usePointStyle: true,
                        pointStyle: 'circle',
                        font: {
                            family: "'Inter', sans-serif",
                            size: 11,
                            weight: '500'
                        }
                    }
                },
                tooltip: {
                    enabled: true,
                    backgroundColor: 'rgba(15, 23, 42, 0.95)',
                    titleColor: '#ffffff',
                    bodyColor: '#e2e8f0',
                    borderColor: 'rgba(59, 130, 246, 0.4)',
                    borderWidth: 1,
                    padding: 16,
                    cornerRadius: 12,
                    callbacks: {
                        label: function (context) {
                            return '📊 ' + context.label + ': ' + context.raw + ' services';
                        }
                    }
                }
            }
        }
    });

    // Value Score Chart with enhanced gradient and modern SaaS styling
    valueChartInstance = new Chart(valueCtx, {
        type: "bar",
        data: {
            labels: platformLabels,
            datasets: [{
                label: "Value Score",
                data: platformData.map(p => p.avgScore),
                backgroundColor: platformBgColors.map(c => createGradient(valueCtx, c)),
                borderColor: 'transparent',
                borderWidth: 0,
                borderRadius: 0, // Sharp square bars
                borderSkipped: false,
                hoverBackgroundColor: platformBgColors.map(c => createGlowGradient(valueCtx, c)),
                hoverBorderWidth: 0,
                barPercentage: 0.7,
                categoryPercentage: 0.8
            }]
        },
        options: {
            ...getChartOptions("Value"),
            scales: {
                ...getChartOptions("Value").scales,
                y: {
                    ...getChartOptions("Value").scales.y,
                    max: 10
                }
            },
            plugins: {
                ...getChartOptions("Value").plugins,
                tooltip: {
                    enabled: true,
                    backgroundColor: 'rgba(15, 23, 42, 0.95)',
                    titleColor: '#ffffff',
                    bodyColor: '#e2e8f0',
                    borderColor: 'rgba(59, 130, 246, 0.3)',
                    borderWidth: 1,
                    padding: 14,
                    cornerRadius: 10,
                    callbacks: {
                        label: function (context) {
                            return "Value: " + context.raw.toFixed(1);
                        }
                    }
                }
            }
        }
    });

    // Popularity Chart with enhanced gradient and modern SaaS styling
    popularityChartInstance = new Chart(popCtx, {
        type: "bar",
        data: {
            labels: platformLabels,
            datasets: [{
                label: "Popularity",
                data: platformData.map(p => p.avgPopularity),
                backgroundColor: platformBgColors.map(c => createGradient(popCtx, c)),
                borderColor: 'transparent',
                borderWidth: 0,
                borderRadius: 0, // Sharp square bars
                borderSkipped: false,
                hoverBackgroundColor: platformBgColors.map(c => createGlowGradient(popCtx, c)),
                hoverBorderWidth: 0,
                barPercentage: 0.7,
                categoryPercentage: 0.8
            }]
        },
        options: {
            ...getChartOptions("Popularity"),
            scales: {
                ...getChartOptions("Popularity").scales,
                y: {
                    ...getChartOptions("Popularity").scales.y,
                    max: 10
                }
            },
            plugins: {
                ...getChartOptions("Popularity").plugins,
                tooltip: {
                    enabled: true,
                    backgroundColor: 'rgba(15, 23, 42, 0.95)',
                    titleColor: '#ffffff',
                    bodyColor: '#e2e8f0',
                    borderColor: 'rgba(59, 130, 246, 0.3)',
                    borderWidth: 1,
                    padding: 14,
                    cornerRadius: 10,
                    callbacks: {
                        label: function (context) {
                            return "Popularity: " + context.raw.toFixed(1);
                        }
                    }
                }
            }
        }
    });
}

// Display results table
function displayTable(services) {
    const tbody = document.getElementById('resultsBody');

    // Clear existing rows
    tbody.innerHTML = "";

    // Add rows for each service
    services.forEach(s => {
        const row = document.createElement("tr");

        // Get rank badge class
        let rankClass = "rank-other";
        if (s.rank === 1) rankClass = "rank-1";
        else if (s.rank === 2) rankClass = "rank-2";
        else if (s.rank === 3) rankClass = "rank-3";

        // Get performance class
        let perfClass = "perf-low";
        if (s.performanceLevel === "High") perfClass = "perf-high";
        else if (s.performanceLevel === "Medium") perfClass = "perf-medium";

        // Resolve the real cloud provider URL for this service
        const tableServiceUrl = getServiceUrl(s.platform, s.service_name);
        const serviceNameHtml = tableServiceUrl
            ? `<a href="${tableServiceUrl}" target="_blank" rel="noopener noreferrer" class="table-service-link">${s.service_name} <i class="fas fa-external-link-alt" style="font-size: 0.7em; opacity: 0.6;"></i></a>`
            : s.service_name;

        row.innerHTML = `
            <td><span class="rank-badge ${rankClass}">#${s.rank}</span></td>
            <td>
                <span class="platform-badge" style="background: ${platformColors[s.platform]}">
                    ${renderLogoImg({ url: getProviderLogoUrl(s.platform), alt: s.platform, sizePx: 18 })}
                    <span style="margin-left:4px;">${s.platform}</span>
                </span>
            </td>
            <td>${serviceNameHtml}</td>

            <td>${s.cpu || '-'}</td>
            <td>${s.ram || '-'}</td>
            <td>${s.storage || '-'}</td>
            <td><strong>$${s.cost_per_hour.toFixed(4)}</strong></td>
            <td><strong>$${s.cost_per_day.toFixed(2)}</strong></td>
            <td><strong>$${s.cost_per_week.toFixed(2)}</strong></td>
            <td><strong>$${s.cost_per_month.toFixed(2)}</strong></td>
            <td><span class="${perfClass}">${s.performanceLevel}</span></td>
            <td>${(s.popularity_score || 5).toFixed(1)}/10</td>
            <td><strong>${s.score.toFixed(1)}</strong></td>
        `;

        tbody.appendChild(row);
    });
}

// Sort table (Data-driven instead of DOM-driven)
function sortTable(sortBy) {
    if (!currentServices || currentServices.length === 0) return;

    // Create a clone to safely sort and update UI ranks
    let sortedList = [...currentServices];

    if (sortBy === 'cost') {
        sortedList.sort((a, b) => {
            const costA = a.cost || a.cost_per_month || 0;
            const costB = b.cost || b.cost_per_month || 0;
            return costA - costB;
        });
    } else {
        sortedList.sort((a, b) => {
            const scoreA = a.score || 0;
            const scoreB = b.score || 0;
            return scoreB - scoreA;
        });
    }

    // Smoothly re-render table 
    displayTable(sortedList);
}

// Show loading state
function showLoading() {
    document.getElementById('loadingState').style.display = 'flex';
    document.getElementById('resultsSection').style.display = 'none';
    document.getElementById('emptyState').style.display = 'none';
}

// Hide loading state
function hideLoading() {
    document.getElementById('loadingState').style.display = 'none';
}

// Show error message
function showError(message) {
    alert(message);
}

// Display provider statistics
function displayProviderStats(stats) {
    const grid = document.getElementById('providerStatsGrid');
    if (!grid || !stats) return;

    grid.innerHTML = '';
    stats.forEach(stat => {
        const card = document.createElement('div');
        card.className = 'provider-stat-card';
        card.style.background = 'rgba(15, 23, 42, 0.6)';
        card.style.border = '1px solid rgba(255, 255, 255, 0.05)';
        card.style.borderRadius = '12px';
        card.style.padding = '1.5rem';
        card.style.display = 'flex';
        card.style.flexDirection = 'column';
        card.style.gap = '0.5rem';
        
        // EXCELLENCE: Make stat cards clickable filters
        card.title = `Click to filter results for ${stat.platform}`;
        card.onclick = () => filterByProvider(stat.platform);

        card.innerHTML = `
            <div style="display: flex; align-items: center; gap: 0.5rem; margin-bottom: 0.5rem;">
                <span style="font-size: 1.25rem; display:inline-flex; align-items:center; justify-content:center;">
                    ${renderLogoImg({ url: getProviderLogoUrl(stat.platform), alt: stat.platform, sizePx: 28 })}
                </span>

                <strong style="font-size: 1.1rem; color: white;">${stat.platform}</strong>
            </div>
            <div style="display: flex; justify-content: space-between; font-size: 0.9rem;">
                <span style="color: #94a3b8;">Services:</span>
                <span style="color: white; font-weight: 500;">${stat.count}</span>
            </div>
            <div style="display: flex; justify-content: space-between; font-size: 0.9rem;">
                <span style="color: #94a3b8;">Avg Cost:</span>
                <span style="color: white; font-weight: 500;">$${stat.avgCost.toFixed(2)}/mo</span>
            </div>
            <div style="display: flex; justify-content: space-between; font-size: 0.9rem;">
                <span style="color: #94a3b8;">Avg Performance:</span>
                <span style="color: white; font-weight: 500;">${stat.avgPerformance.toFixed(1)}/10</span>
            </div>
        `;
        grid.appendChild(card);
    });
}

// EXCELLENCE: Provider Filtering Logic
function filterByProvider(platform) {
    console.log("Filtering results for provider:", platform);
    
    // Update quick-filter chips UI
    const chips = document.querySelectorAll('.provider-chip');
    chips.forEach(chip => {
        if (chip.dataset.provider === platform) chip.classList.add('active');
        else chip.classList.remove('active');
    });

    if (!currentServices || currentServices.length === 0) return;

    let filtered = [];
    if (platform === 'all') {
        filtered = currentServices;
    } else {
        filtered = currentServices.filter(s => s.platform === platform);
    }

    // Refresh display with filtered data
    displayRecommendations(filtered);
    displayTable(filtered);
    
    // Scroll to results
    document.getElementById('resultsSection').scrollIntoView({ behavior: 'smooth', block: 'start' });
}

// EXCELLENCE: Initialize results filters
function setupProviderFilters() {
    const chips = document.querySelectorAll('.provider-chip');
    chips.forEach(chip => {
        chip.onclick = () => filterByProvider(chip.dataset.provider);
    });

    // Also hook up footer links to trigger filter (stay in app)
    const footerBadges = document.querySelectorAll('.footer .provider-badge');
    footerBadges.forEach(badge => {
        badge.addEventListener('click', function(e) {
            const provider = this.textContent.trim().split(' ')[0]; // AWS, GCP, Azure, etc.
            // If results are visible, filter them
            if (document.getElementById('resultsSection').style.display !== 'none') {
                e.preventDefault(); // Don't navigate away if we have results
                filterByProvider(provider);
            }
        });
    });
}

// ============================================================
// AI COMPARE LOGIC
// ============================================================
function toggleView(view) {
    const cloudBtn = document.getElementById('btn-view-cloud');
    const aiBtn = document.getElementById('btn-view-ai');
    const cloudView = document.getElementById('cloudView');
    const aiView = document.getElementById('aiView');

    if (view === 'cloud') {
        cloudBtn.classList.add('active');
        aiBtn.classList.remove('active');
        cloudView.style.display = 'block';
        aiView.style.display = 'none';

        // ensure correct results show up if previously completed
        document.getElementById('aiResultsSection').style.display = 'none';
        if (currentServices && currentServices.length > 0 && !isComparing) {
            document.getElementById('resultsSection').style.display = 'block';
        }
    } else {
        aiBtn.classList.add('active');
        cloudBtn.classList.remove('active');
        aiView.style.display = 'block';
        cloudView.style.display = 'none';

        document.getElementById('resultsSection').style.display = 'none';
    }
}

async function compareAiTools() {
    if (isComparing) return;

    const purpose = document.getElementById('aiPurpose').value;
    const queryTextEl = document.getElementById('aiQueryText');
    const queryText = queryTextEl ? (queryTextEl.value || '').trim() : '';


    const compareBtn = document.getElementById('btnCompareAi');

    compareBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Analyzing...';
    compareBtn.style.opacity = '0.7';
    compareBtn.style.cursor = 'not-allowed';
    isComparing = true;

    // We can reuse the main loading UI for AI too
    document.getElementById('loadingState').style.display = 'flex';
    document.getElementById('aiResultsSection').style.display = 'none';
    document.getElementById('aiEmptyState').style.display = 'none';

    try {
        const token = localStorage.getItem('token');
        const headers = {
            "Content-Type": "application/json"
        };
        if (token) {
            headers["Authorization"] = `Bearer ${token}`;
        }

        const res = await fetch(`${API_BASE_URL}/api/ai-compare`, {
            method: "POST",
            headers: headers,
            body: JSON.stringify({
                purpose: purpose,
                queryText: queryText
            })
        });

        if (!res.ok) {
            if (res.status === 401 || res.status === 403) {
                // Do not redirect
                localStorage.removeItem('token');
                localStorage.removeItem('userName');
            }
            throw new Error(`HTTP error! status: ${res.status}`);
        }

        const result = await res.json();
        if (!result.success || result.error) throw new Error(result.error || "AI Analysis failed");
        
        const apiData = result.data;
        const tools = apiData.tools || [];
        if (tools.length === 0) {
            showError("No AI tools found for the category");
            return;
        }

        hideLoading();
        displayAiRecommendations(tools);
        document.getElementById('aiResultsSection').style.display = 'block';

    } catch (error) {
        console.error("AI Compare error:", error);
        hideLoading();
        showError("Failed to compare AI tools. Please try again.\n" + error.message);
        document.getElementById('aiEmptyState').style.display = 'block';
    } finally {
        isComparing = false;
        compareBtn.innerHTML = '<i class="fas fa-magic"></i> Compare AI Tools';
        compareBtn.style.opacity = '1';
        compareBtn.style.cursor = 'pointer';
    }
}

function displayAiRecommendations(tools) {
    const grid = document.getElementById('aiRecommendationsGrid');
    grid.innerHTML = '';

    tools.forEach((tool, index) => {
        let rankBadgeStyle = `background: rgba(148, 163, 184, 0.2); color: #cbd5e1; border: 1px solid rgba(148, 163, 184, 0.5);`;
        if (index === 0) rankBadgeStyle = `background: linear-gradient(135deg, rgba(0, 212, 170, 0.2), rgba(0, 184, 148, 0.2)); color: #00d4aa; border: 1px solid rgba(0, 212, 170, 0.5);`;
        else if (index === 1) rankBadgeStyle = `background: linear-gradient(135deg, rgba(124, 58, 237, 0.2), rgba(139, 92, 246, 0.2)); color: #a78bfa; border: 1px solid rgba(124, 58, 237, 0.5);`;
        else if (index === 2) rankBadgeStyle = `background: linear-gradient(135deg, rgba(59, 130, 246, 0.2), rgba(96, 165, 250, 0.2)); color: #60a5fa; border: 1px solid rgba(59, 130, 246, 0.5);`;

        // Resolve AI tool URL
        const toolUrl = getAiToolUrl(tool.tool_name);
        const tryToolBtnHtml = toolUrl
            ? `<a href="${toolUrl}" target="_blank" rel="noopener noreferrer" class="visit-service-btn" style="border-color: rgba(0, 212, 170, 0.5); color: #00d4aa;">
                   <i class="fas fa-external-link-alt"></i> Try Tool
               </a>`
            : '';

        const card = document.createElement('div');
        card.className = 'recommendation-card';
        card.innerHTML = `
            <div class="recommendation-badge" style="${rankBadgeStyle}">
                <i class="fas fa-trophy"></i> #${tool.rank} Recommendation
            </div>
            <div class="recommendation-content">
                <div class="recommendation-platform">
                    <h3 style="flex-wrap: wrap;">${tool.tool_name} <span style="font-size: 0.8em; color: #94a3b8; font-weight: normal;">by ${tool.provider}</span></h3>
                </div>
                </div>
                <p style="color: #e2e8f0; font-size: 0.95rem; line-height: 1.5; margin: 0.5rem 0;">${tool.description}</p>
                <div class="recommendation-stats" style="margin-top: auto; flex-wrap: wrap; gap: 1rem;">
                    <div class="stat-item" style="text-align: left; flex: 1; min-width: 120px;">
                        <span class="stat-label">Model Engine</span>
                        <span class="stat-value" style="font-size: 1rem; color: #fff;">${tool.model_number}</span>
                    </div>
                    <div class="stat-item" style="text-align: left; flex: 1; min-width: 120px;">
                        <span class="stat-label">Pricing</span>
                        <span class="stat-value" style="font-size: 1rem; color: #00d4aa;">${tool.pricing}</span>
                    </div>
                    </div>
                    <div class="stat-item" style="text-align: right;">
                        <span class="stat-label">Rating</span>
                        <span class="stat-value performance" style="font-size: 1.25rem;">${tool.score.toFixed(1)}/10</span>
                    </div>
                </div>
                ${tryToolBtnHtml}
            </div>
        `;
        grid.appendChild(card);
    });

    // Render AI Tools Rating Chart
    if (aiRatingChartInstance) {
        aiRatingChartInstance.destroy();
    }
    const ctx = document.getElementById('aiRatingChart');
    if (ctx) {
        const labels = tools.map(t => t.tool_name);
        const data = tools.map(t => t.score);
        
        const bgColors = [
            'rgba(0, 212, 170, 0.6)',
            'rgba(124, 58, 237, 0.6)',
            'rgba(59, 130, 246, 0.6)',
            'rgba(251, 191, 36, 0.6)',
            'rgba(16, 185, 129, 0.6)'
        ];
        const borderColors = [
            'rgba(0, 212, 170, 1)',
            'rgba(124, 58, 237, 1)',
            'rgba(59, 130, 246, 1)',
            'rgba(251, 191, 36, 1)',
            'rgba(16, 185, 129, 1)'
        ];

        aiRatingChartInstance = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Rating (out of 10)',
                    data: data,
                    backgroundColor: bgColors.slice(0, tools.length),
                    borderColor: borderColors.slice(0, tools.length),
                    borderWidth: 1,
                    borderRadius: 4
                }]
            },
            options: {
                ...globalChartOptions,
                indexAxis: 'y', // Horizontal bar chart
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                return ` Rating: ${context.raw.toFixed(1)}/10`;
                            }
                        }
                    }
                },
                scales: {
                    x: {
                        beginAtZero: true,
                        max: 10,
                        grid: { color: "rgba(0, 212, 170, 0.06)" },
                        ticks: { color: "#94a3b8" }
                    },
                    y: {
                        grid: { display: false },
                        ticks: { color: "#94a3b8", font: { size: 12 } }
                    }
                }
            }
        });
    }
}

// ============================================================
// THEME TOGGLE (Dark / Light)
// ============================================================
(function initThemeToggle() {
    const toggle = document.getElementById('themeToggle');
    const icon = document.getElementById('themeIcon');
    if (!toggle || !icon) return;

    // Restore saved preference
    const saved = localStorage.getItem('cc-theme');
    if (saved === 'light') {
        document.body.classList.add('light-theme');
        icon.classList.replace('fa-sun', 'fa-moon');
    }

    toggle.addEventListener('click', () => {
        const isLight = document.body.classList.toggle('light-theme');
        icon.classList.replace(
            isLight ? 'fa-sun' : 'fa-moon',
            isLight ? 'fa-moon' : 'fa-sun'
        );
        localStorage.setItem('cc-theme', isLight ? 'light' : 'dark');

        // Update Chart.js grid/tick colors for the new theme
        const tickColor = isLight ? '#475569' : '#94a3b8';
        const gridColor = isLight ? 'rgba(0,212,170,0.06)' : 'rgba(0,212,170,0.06)';
        [costChartInstance, performanceChartInstance, rankingChartInstance,
            distributionChartInstance, valueChartInstance, popularityChartInstance,
            trendChartInstance, aiRatingChartInstance].forEach(chart => {
                if (!chart) return;
                if (chart.options.scales?.y) {
                    chart.options.scales.y.ticks.color = tickColor;
                    chart.options.scales.y.grid.color = gridColor;
                }
                if (chart.options.scales?.x) {
                    chart.options.scales.x.ticks.color = tickColor;
                }
                chart.update('none');
            });
    });
})();

// Export for potential testing
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        compare,
        displayRecommendation,
        displayCharts,
        displayTable,
        sortTable
    };
}

async function generateAiSummary(services) {
    const summaryCard = document.getElementById('aiSummaryCard');
    const summaryContent = document.getElementById('aiSummaryContent');

    if (!summaryCard || !summaryContent) return;

    summaryCard.style.display = 'block';
    summaryContent.innerHTML = '<div class="typing-indicator"><span></span><span></span><span></span></div>';

    // Simulate thinking/generating
    setTimeout(() => {
        const bestCost = services.reduce((min, p) => p.cost < min.cost ? p : min, services[0]);
        const bestPerf = services.reduce((max, p) => p.performance_score > max.performance_score ? p : max, services[0]);

        let verdict = `Based on my architectural analysis, `;
        if (bestCost.platform === bestPerf.platform) {
            verdict += `<strong>${bestCost.platform}</strong> is the clear leader for this workload, offering both the lowest cost ($${bestCost.cost.toFixed(2)}) and peak performance.`;
        } else {
            verdict += `you face a trade-off: <strong>${bestCost.platform}</strong> is the budget leader at $${bestCost.cost.toFixed(2)}, but <strong>${bestPerf.platform}</strong> delivers superior performance (Score: ${bestPerf.performance_score}/10).`;
        }

        verdict += `<br><br>Recommended Action: Deploy to <strong>${bestPerf.platform}</strong> if uptime and throughput are critical; otherwise, <strong>${bestCost.platform}</strong> provides the optimal ROI.`;

        summaryContent.innerHTML = verdict;
    }, 1200);
}

// ============================================================
// CHATBOTS (EXTRA UI) - additive, does not change existing compare flows
// ============================================================
let chatMode = 'cloud'; // 'cloud' | 'ai-tools'
let lastAiTools = [];

function setChatMode(mode) {
    chatMode = mode;

    const btnCloud = document.getElementById('btnChatCloud');
    const btnAiTools = document.getElementById('btnChatAiTools');

    if (btnCloud && btnAiTools) {
        btnCloud.classList.toggle('active', mode === 'cloud');
        btnAiTools.classList.toggle('active', mode === 'ai-tools');
    }

    // Lazy set button defaults if active styles were not already applied
    const input = document.getElementById('chatInput');
    if (input && !input.value) {
        input.value = mode === 'cloud'
            ? 'Explain why the top recommendation is best for my workload.'
            : 'Explain the top AI tool recommendation for my selected purpose.';
    }

    // Optional: clear old chat view when switching modes
    const chatMessages = document.getElementById('chatMessages');
    if (chatMessages) {
        // Keep existing history if desired; for now we keep it to avoid disrupting user flow
    }
}

function appendChatMessage({ role, content }) {
    const container = document.getElementById('chatMessages');
    if (!container) return;

    const msg = document.createElement('div');
    msg.style.padding = '0.9rem 1rem';
    msg.style.borderRadius = '14px';
    msg.style.border = '1px solid rgba(255,255,255,0.06)';
    msg.style.background = role === 'user'
        ? 'rgba(0, 212, 170, 0.10)'
        : 'rgba(124, 58, 237, 0.08)';
    msg.style.color = 'white';

    msg.innerHTML = `<div style="font-weight:700; color:#94a3b8; font-size:0.85rem; margin-bottom:0.35rem;">${role === 'user' ? 'You' : 'Architect'}</div>
                      <div style="white-space: pre-wrap; line-height:1.55;">${content}</div>`;

    container.appendChild(msg);
    container.scrollIntoView({ behavior: 'smooth', block: 'end' });
}

async function sendChatMessage() {
    const input = document.getElementById('chatInput');
    if (!input) return;

    const question = (input.value || '').trim();
    if (!question) {
        alert('Please type a question for the chatbot.');
        return;
    }

    // Basic auth propagation (if no token, backend will reject)
    const token = localStorage.getItem('token');
    const headers = { 'Content-Type': 'application/json' };
    if (token) headers['Authorization'] = `Bearer ${token}`;

    // Seed context based on last known results (no changes to compare UI)
    const cloudContext = currentServices && currentServices.length ? {
        category: (document.querySelector('.category-btn.active')?.dataset?.category || 'compute'),
        services: currentServices
    } : null;

    const aiToolsContext = lastAiTools && lastAiTools.length ? { tools: lastAiTools } : null;

    appendChatMessage({ role: 'user', content: question });
    input.value = '';

    const chatEndpoint = chatMode === 'cloud' ? `${API_BASE_URL}/api/chat/cloud` : `${API_BASE_URL}/api/chat/ai-tools`;

    try {
        const res = await fetch(chatEndpoint, {
            method: 'POST',
            headers,
            body: JSON.stringify({
                question,
                cloudContext,
                aiToolsContext
            })
        });

        if (!res.ok) {
            throw new Error(`HTTP ${res.status}`);
        }

        const result = await res.json();
        if (!result.success || result.error) {
            throw new Error(result.error || 'Chat failed');
        }

        const reply = result.data?.reply || 'No reply generated.';
        appendChatMessage({ role: 'assistant', content: reply });

    } catch (e) {
        appendChatMessage({ role: 'assistant', content: 'Chat failed: ' + (e.message || e) });
    }
}

// Enhance AI tools compare to remember latest results for chatbot context
const _oldDisplayAiRecommendations = displayAiRecommendations;
displayAiRecommendations = function(tools) {
    lastAiTools = tools || [];
    return _oldDisplayAiRecommendations(tools);
};

function exportToPDF() {
    if (typeof html2pdf === 'undefined') {
        alert('PDF generator library is not loaded. Please try again later.');
        return;
    }

    const element = document.getElementById('resultsSection');
    
    // Temporarily adjust styling for better PDF rendering
    const originalBackground = element.style.background;
    element.style.background = '#0f172a'; // Ensure dark background is captured
    element.style.padding = '20px';
    
    // Hide buttons we don't want in the PDF
    const actionBtns = document.querySelectorAll('.summary-actions, .filter-btn, .sort-select');
    actionBtns.forEach(btn => btn.style.display = 'none');

    // Notify user
    const btn = document.querySelector('button[onclick="exportToPDF()"]');
    const originalText = btn.innerHTML;
    btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Generating...';
    btn.disabled = true;

    const opt = {
        margin:       10,
        filename:     `CloudCompare_Architect_Report_${new Date().toISOString().split('T')[0]}.pdf`,
        image:        { type: 'jpeg', quality: 0.98 },
        html2canvas:  { scale: 2, useCORS: true, logging: false },
        jsPDF:        { unit: 'mm', format: 'a4', orientation: 'landscape' }
    };

    html2pdf().set(opt).from(element).save().then(() => {
        // Restore styling and buttons
        element.style.background = originalBackground;
        element.style.padding = '';
        actionBtns.forEach(btn => btn.style.display = '');
        
        btn.innerHTML = originalText;
        btn.disabled = false;
    }).catch(err => {
        console.error('PDF generation error:', err);
        alert('Failed to generate PDF report.');
        
        // Restore styling and buttons on error too
        element.style.background = originalBackground;
        element.style.padding = '';
        actionBtns.forEach(btn => btn.style.display = '');
        btn.innerHTML = originalText;
        btn.disabled = false;
    });
}

function exportToCSV() {
    const data = currentServices.map(s => `${s.platform},${s.service_name},${s.cost},${s.performance_score},${s.score}`).join('\n');
    const blob = new Blob(['Platform,Service,Cost,Performance,Score\n' + data], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.setAttribute('href', url);
    a.setAttribute('download', 'CloudCompare_Architect_Report.csv');
    a.click();
}
