// app/providers.tsx
'use client'

import {NextUIProvider} from '@nextui-org/react';
import {useRouter} from 'next/navigation'
import NprogressProvider from "@/lib/NProgressProvider";

export function Providers({children}) {
    const router = useRouter();

    return (
        <NprogressProvider>
            <NextUIProvider navigate={router.push}>
                {children}
            </NextUIProvider>
        </NprogressProvider>
    )
}
