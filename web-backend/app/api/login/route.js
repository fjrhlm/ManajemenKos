import { supabase } from '@/lib/supabase';
import { parseBody } from '@/lib/parseBody';
import { NextResponse } from 'next/server';
import bcrypt from 'bcryptjs';

export async function POST(request) {
  try {
    const { email, password } = await parseBody(request);

    const { data: user, error } = await supabase
      .from('users')
      .select('*')
      .eq('email', email)
      .single();

    if (error || !user) {
      return NextResponse.json({ status: 'error', message: 'Email tidak ditemukan' }, { status: 401 });
    }

    // Support password plain text (dari SQL Editor) dan bcrypt hash (dari register)
    const isBcrypt = user.password && user.password.startsWith('$2');
    let valid = false;
    if (isBcrypt) {
      valid = await bcrypt.compare(password, user.password);
    } else {
      valid = (password === user.password);
    }
    if (!valid) {
      return NextResponse.json({ status: 'error', message: 'Password salah' }, { status: 401 });
    }

    return NextResponse.json({
      status: 'success',
      message: 'Login berhasil',
      data: {
        id_user: user.id,
        nama: user.nama,
        email: user.email,
        role: user.role
      }
    });
  } catch (e) {
    return NextResponse.json({ status: 'error', message: e.message }, { status: 500 });
  }
}
