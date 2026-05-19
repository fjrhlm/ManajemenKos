import { supabase } from '@/lib/supabase';
import { NextResponse } from 'next/server';
import crypto from 'crypto';

export async function POST(request) {
  try {
    const body = await request.json();
    const { order_id, transaction_status, gross_amount, signature_key, status_code } = body;
    
    // Verifikasi signature dari Midtrans untuk keamanan
    const serverKey = process.env.MIDTRANS_SERVER_KEY;
    const hashString = order_id + status_code + gross_amount + serverKey;
    const expectedSignature = crypto.createHash('sha512').update(hashString).digest('hex');
    
    if (expectedSignature !== signature_key) {
      return NextResponse.json({ message: 'Invalid signature' }, { status: 403 });
    }
    
    // Jika pembayaran berhasil (settlement / capture)
    if (transaction_status == 'capture' || transaction_status == 'settlement') {
      // Ambil ID tagihan dari order_id (Format: TAGIHAN-{id_tagihan}-{timestamp})
      const idTagihanStr = order_id.split('-')[1];
      const id_tagihan = parseInt(idTagihanStr);
      
      // Update status bayar di database menjadi Lunas
      await supabase.from('tagihan')
        .update({ status_bayar: 'Lunas', bukti_transfer: 'Midtrans Auto-Verified' })
        .eq('id_tagihan', id_tagihan);
    }
    
    return NextResponse.json({ status: 'success' });
  } catch (e) {
    return NextResponse.json({ status: 'error', message: e.message }, { status: 500 });
  }
}
