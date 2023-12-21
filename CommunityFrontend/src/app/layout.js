import './globals.css'
import {Providers} from "./providers";

export const metadata = {
    title: '食堂点评交流社区',
    description: '上海理工大学食堂点评交流社区可以让你在这里发表你对食堂的看法。',
}

export default function RootLayout({children}) {
    return (
        <html lang="zh" className='light'>
        <body>
        <Providers>
            {children}
        </Providers>
        </body>
        </html>
    )
}
