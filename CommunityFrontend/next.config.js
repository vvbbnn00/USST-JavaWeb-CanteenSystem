/** @type {import('next').NextConfig} */
const nextConfig = {
    images: {
        remotePatterns: [{
            protocol: 'https',
            hostname: 'oss.bzpl.tech',
            port: '',
            pathname: '/**',
        }]
    }
}

module.exports = nextConfig
