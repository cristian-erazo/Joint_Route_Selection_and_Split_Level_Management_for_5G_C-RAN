graph [
  node [
    id 0
    label "0"
    type 2
    prc 3
  ]
  node [
    id 1
    label "1"
    type 2
    prc 5
  ]
  node [
    id 2
    label "2"
    type 1
    prc 1
    ant 3
    prb 6
    x 46
    y 28
  ]
  node [
    id 3
    label "3"
    type 1
    prc 2
    ant 10
    prb 5
    x 22
    y 22
  ]
  node [
    id 4
    label "4"
    type 1
    prc 4
    ant 6
    prb 6
    x 110
    y 29
  ]
  node [
    id 5
    label "5"
    type 1
    prc 3
    ant 7
    prb 2
    x 77
    y 88
  ]
  node [
    id 6
    label "6"
    type 1
    prc 4
    ant 4
    prb 3
    x 50
    y 36
  ]
  edge [
    source 0
    target 3
    bandwith 633
    delay 155
  ]
  edge [
    source 1
    target 2
    bandwith 940
    delay 376
  ]
  edge [
    source 1
    target 4
    bandwith 542
    delay 256
  ]
  edge [
    source 1
    target 5
    bandwith 231
    delay 109
  ]
  edge [
    source 1
    target 6
    bandwith 906
    delay 433
  ]
]
