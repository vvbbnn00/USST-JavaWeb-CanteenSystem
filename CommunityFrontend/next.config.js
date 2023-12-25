/** @type {import('next').NextConfig} */
const nextConfig = {
    output: 'standalone',
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
