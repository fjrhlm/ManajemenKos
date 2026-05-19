import { supabase } from '@/lib/supabase';
import { parseBody } from '@/lib/parseBody';
import { NextResponse } from 'next/server';
import midtransClient from 'midtrans-client';

let snap = new midtransClient.Snap({
  isProduction : false,
  serverKey : process.env.MIDTRANS_SERVER_KEY,
  clientKey : process.env.NEXT_PUBLIC_MIDTRANS_CLIENT_KEY
});

export async function POST(request) {
  try {
    const { id_tagihan } = await parseBody(request);

    if (!id_tagihan) {
        return NextResponse.json({ status: 'error', message: 'ID Tagihan diperlukan' }, { status: 400 });
    }

    // 1. Ambil data tagihan terlebih dahulu tanpa join untuk mencegah error relasi
    const { data: tagihan, error } = await supabase
      .from('tagihan')
      .select('*')
      .eq('id_tagihan', id_tagihan)
      .single();

    if (error || !tagihan) {
      console.error("Supabase Error:", error);
      return NextResponse.json({ status: 'error', message: 'Tagihan tidak ditemukan' }, { status: 404 });
    }

    // Ambil data user secara terpisah
    const { data: user } = await supabase
      .from('users')
      .select('nama, email')
      .eq('id', tagihan.id_user)
      .single();

    // Jika sudah lunas, tolak
    if (tagihan.status_bayar === 'Lunas') {
        return NextResponse.json({ status: 'error', message: 'Tagihan ini sudah lunas' }, { status: 400 });
    }

    // 2. Siapkan parameter Midtrans Snap
    let parameter = {
      "transaction_details": {
        // Order ID harus unik, kita gabungkan ID Tagihan dengan timestamp
        "order_id": `TAGIHAN-${tagihan.id_tagihan}-${Date.now()}`,
        "gross_amount": parseInt(tagihan.nominal)
      },
      "customer_details": {
        "first_name": user?.nama || "Penghuni",
        "email": user?.email || "penghuni@kos.com"
      }
    };

    // 3. Request Token dan URL Snap dari Midtrans
    const transaction = await snap.createTransaction(parameter);

    return NextResponse.json({ 
        status: 'success', 
        token: transaction.token,
        redirect_url: transaction.redirect_url
    });

  } catch (e) {
    console.error("Midtrans Error:", e);
    return NextResponse.json({ status: 'error', message: e.message }, { status: 500 });
  }
}
