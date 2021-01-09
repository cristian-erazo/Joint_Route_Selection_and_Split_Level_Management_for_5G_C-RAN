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
    type 2
    prc 3
  ]
  node [
    id 3
    label "3"
    type 1
    prc 5
    ant 2
    prb 3
    x 78
    y 20
  ]
  node [
    id 4
    label "4"
    type 1
    prc 2
    ant 4
    prb 5
    x 50
    y 69
  ]
  node [
    id 5
    label "5"
    type 1
    prc 4
    ant 7
    prb 3
    x 54
    y 100
  ]
  node [
    id 6
    label "6"
    type 1
    prc 5
    ant 3
    prb 5
    x 64
    y 22
  ]
  edge [
    source 0
    target 4
    bandwith 796
    delay 449
  ]
  edge [
    source 1
    target 3
    bandwith 994
    delay 398
  ]
  edge [
    source 2
    target 5
    bandwith 437
    delay 256
  ]
  edge [
    source 2
    target 6
    bandwith 385
    delay 296
  ]
]
