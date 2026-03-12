// API Utility Functions
const API = {
    // Base request handler
    async request(url, options = {}) {
        const defaultOptions = {
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            }
        };

        console.log(`[API Request] ${options.method || 'GET'} ${url}`, options.body ? JSON.parse(options.body) : '');
        const response = await fetch(url, { ...defaultOptions, ...options });

        if (response.status === 401 || response.status === 403) {
            window.location.href = '/login?error=unauthorized';
            return new Promise(() => { }); // Return a pending promise to halt further execution
        }

        if (!response.ok) {
            const error = await response.json().catch(() => ({ error: 'Request failed' }));
            throw new Error(error.error || error.message || 'Request failed');
        }

        return response.json();
    },

    // GET request
    get(url) {
        return this.request(url, { method: 'GET' });
    },

    // POST request
    post(url, data) {
        return this.request(url, {
            method: 'POST',
            body: JSON.stringify(data)
        });
    },

    // PUT request
    put(url, data) {
        return this.request(url, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    },

    // DELETE request
    delete(url) {
        return this.request(url, { method: 'DELETE' });
    }
};

// Show loading spinner
function showLoading(elementId) {
    const element = document.getElementById(elementId);
    if (element) {
        element.innerHTML = '<div class="text-center"><div class="spinner"></div><p class="mt-2">Loading...</p></div>';
    }
}

// Show error message
function showError(message, elementId = null) {
    const errorHtml = `
        <div class="alert alert-error">
            ${message}
        </div>
    `;

    if (elementId) {
        const element = document.getElementById(elementId);
        if (element) {
            element.innerHTML = errorHtml;
        } else {
            alert(message);
        }
    } else {
        alert(message);
        console.error(message);
    }
}

// Show success message
function showSuccess(message) {
    const successDiv = document.createElement('div');
    successDiv.className = 'alert alert-success';
    successDiv.textContent = message;
    successDiv.style.position = 'fixed';
    successDiv.style.top = '20px';
    successDiv.style.right = '20px';
    successDiv.style.zIndex = '9999';
    successDiv.style.minWidth = '300px';

    document.body.appendChild(successDiv);

    setTimeout(() => {
        successDiv.style.opacity = '0';
        successDiv.style.transition = 'opacity 0.5s';
        setTimeout(() => successDiv.remove(), 500);
    }, 4000);
}

// Format date
function formatDate(dateString) {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

// Format score with color
function formatScore(score) {
    const numScore = parseFloat(score);
    let colorClass = 'text-success';

    if (numScore < 10) {
        colorClass = 'text-danger';
    } else if (numScore < 12) {
        colorClass = 'text-warning';
    }

    return `<span class="${colorClass}"><strong>${score}/20</strong></span>`;
}

// Logout function
async function logout() {
    if (confirm('Are you sure you want to logout?')) {
        window.location.href = '/logout';
    }
}

// Get current user profile
async function getCurrentProfile() {
    try {
        return await API.get('/api/profile');
    } catch (error) {
        console.error('Error fetching profile:', error);
        return null;
    }
}

// Display user info in navbar
async function displayUserInfo(elementId = 'userInfo') {
    try {
        const profile = await getCurrentProfile();
        if (profile) {
            const userInfoElement = document.getElementById(elementId);
            if (userInfoElement) {
                const roleClass = profile.role.toLowerCase();
                userInfoElement.innerHTML = `
                    <div class="flex align-center gap-md">
                        <div class="text-right">
                            <div class="font-weight-bold">${profile.fullName || profile.username}</div>
                            <div class="badge badge-${roleClass}">${profile.role}</div>
                        </div>
                        <a href="/" class="btn btn-outline btn-sm">🏠 Home</a>
                        <button onclick="logout()" class="btn btn-outline btn-sm">Logout</button>
                    </div>
                `;
            }
        }
    } catch (error) {
        console.error('Error displaying user info:', error);
    }
}
